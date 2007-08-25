package mmrnmhrm.ui.views;

import melnorme.lang.ui.EditorUtil;
import mmrnmhrm.core.model.CompilationUnit;
import mmrnmhrm.core.model.CompilationUnit.EModelStatus;
import mmrnmhrm.ui.DeePlugin;
import mmrnmhrm.ui.DeePluginImages;
import mmrnmhrm.ui.actions.GoToDefinitionHandler;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.ITextEditor;

import descent.internal.compiler.parser.ast.IASTNode;
import dtool.dom.ast.ASTNodeFinder;


/**
 * D AST viewer
 */
public class ASTViewer extends ViewPart implements ISelectionListener,
		IDocumentListener, ISelectionChangedListener, IDoubleClickListener {

	
	public static final String VIEW_ID = "mmrnmhrm.ui.views.ASTViewer";
	
	private IWorkbenchWindow window;

	protected TreeViewer viewer;
	private DrillDownAdapter drillDownAdapter;
	private Action actionExpand;
	private Action actionCollapse;
	private Action actionToggle;
	
	//protected MultiListener fMultiListener;

	protected ITextEditor fEditor;
	protected IDocument fDocument;
	protected CompilationUnit fCUnit;
	protected boolean fUseOldAst = false;
	protected IASTNode selNode;


	public ASTViewer() {
	}
	
		
	@Override
	public void init(IViewSite site) throws PartInitException {
		super.init(site);
		
		window = site.getWorkbenchWindow();
		window.getSelectionService().addPostSelectionListener(this);
		//site.getPage().addPartListener(this);

		/*if (fMultiListener == null) {
			fMultiListener = this.new MultiListener();
			
			ISelectionService service= site.getWorkbenchWindow().getSelectionService();
			service.addPostSelectionListener(fMultiListener);
			site.getPage().addPartListener(fMultiListener);
			//FileBuffers.getTextFileBufferManager().addFileBufferListener(fSuperListener);
		}*/
	}
	
	@Override
	public void dispose() {
		if (fEditor != null && fDocument != null) {
			fDocument.removeDocumentListener(this);
		}
		window.getSelectionService().removePostSelectionListener(this);
		super.dispose();
	}
	
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		drillDownAdapter = new DrillDownAdapter(viewer);
		viewer.setContentProvider(new ASTViewerContentProvider(this));
		viewer.setLabelProvider(new ASTViewerLabelProvider(this));
		viewer.addSelectionChangedListener(this);
		viewer.addDoubleClickListener(this);
		
		makeActions();
		contributeToActionBars();
		hookContextMenu();
		
		selectionChanged(EditorUtil.getActiveEditor(), null);
	}
	
	
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		IWorkbenchPage page = getSite().getWorkbenchWindow().getActivePage();
		if(page == null)
			return;
		
		if(part instanceof ITextEditor) {
			setInput((ITextEditor)part);
		} else if(part == null || page.getActiveEditor() == null){ 
			setInput(null);
		}
	}
	
	public void documentAboutToBeChanged(DocumentEvent event) {
		// Do nothing
	}

	public void documentChanged(DocumentEvent event) {
		refreshViewer();
	}

	public void setInput(ITextEditor editor) {
		if (fEditor != null && fDocument != null) {
			fDocument.removeDocumentListener(this);
		}
		
		IDocument oldDocument = fDocument;
		fEditor = null;
		fDocument = null;
		
		if (editor == null) {
			setContentDescription("No Editor available");
			viewer.getControl().setVisible(false);
		} else {
			fEditor = editor;
			IEditorInput input = fEditor.getEditorInput();
			fCUnit = DeePlugin.getCompilationUnitOperation(input);
			if(fCUnit != null) {
				fDocument = fEditor.getDocumentProvider().getDocument(editor.getEditorInput());
				
				fDocument.addDocumentListener(this);
				if(oldDocument != fDocument) {
					viewer.setInput(fCUnit);
					viewer.getControl().setVisible(true);
				}

				refreshViewer();
			} else {
				setContentDescription("No DeeDocument available");
				viewer.getControl().setVisible(false);
			}
		}
		
	}


	private void refreshViewer() {
		if(fEditor == null || fCUnit == null)
			return;
		
		int offset = EditorUtil.getSelection(fEditor).getOffset();
		if(fCUnit.getParseStatus() == EModelStatus.OK) {
			setContentDescription("AST ok, sel: " + offset);
		} else {
			setContentDescription(fCUnit.toStringParseStatus());
		}
		if(fCUnit.getParseStatus() == CompilationUnit.EModelStatus.PARSER_INTERNAL_ERROR) {
			viewer.getControl().setVisible(false);
			return;
		}
		
		selNode = ASTNodeFinder.findElement(fCUnit.getModule(), offset);
		//viewer.getControl().setRedraw(false);
		//viewer.setInput(fCUnit);
		viewer.refresh();
		if(selNode != null)
			viewer.reveal(selNode);
		//viewer.getControl().setRedraw(true);
	}
	

	
	/** Passing the focus request to the viewer's control. */
	public void setFocus() {
		viewer.getControl().setFocus();
	}
	
	/* ================== Action construction ==================== */

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#DeeASTViewerContext");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				ASTViewer.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu("#DeeASTViewerContext", menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}
	
	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(actionCollapse);
		manager.add(actionExpand);
		manager.add(actionToggle);
		manager.add(new Separator());
		drillDownAdapter.addNavigationActions(manager);
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(actionCollapse);
		manager.add(actionExpand);
		manager.add(new Separator());
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(actionCollapse);
		manager.add(actionExpand);
		manager.add(new Separator());
		drillDownAdapter.addNavigationActions(manager);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}


	private void makeActions() {
		actionExpand = new Action() {
			public void run() {
				viewer.expandAll();
			}
		};
		actionExpand.setText("Expand All");
		actionExpand.setToolTipText("Expand all nodes");
		DeePluginImages.setupActionImages(actionExpand, "expandall.gif");
		
		actionCollapse = new Action() {
			public void run() {
				viewer.collapseAll();
			}
		};
		actionCollapse.setText("Collapse All");
		actionCollapse.setToolTipText("Collapse All nodes");
		DeePluginImages.setupActionImages(actionCollapse, "collapseall.gif");
		
		actionToggle = new Action() {
			public void run() {
				fUseOldAst  = !fUseOldAst; refreshViewer();
			}
		};
		actionToggle.setText("Toggle Neo/Old AST");
		actionToggle.setToolTipText("Toggle Neo/Old AST");
		actionToggle.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
	}



	public void doubleClick(DoubleClickEvent event) {
		GoToDefinitionHandler.executeChecked((AbstractTextEditor)fEditor, true);
	}

	public void selectionChanged(SelectionChangedEvent event) {
		if(fEditor == null)
			return;
		EditorUtil.selectNodeInEditor((AbstractTextEditor)fEditor, event);
	}

}