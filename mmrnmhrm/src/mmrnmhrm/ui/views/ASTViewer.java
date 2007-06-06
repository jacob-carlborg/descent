package mmrnmhrm.ui.views;

import melnorme.lang.ui.EditorUtil;
import mmrnmhrm.core.model.CompilationUnit;
import mmrnmhrm.core.model.EModelStatus;
import mmrnmhrm.ui.DeePluginImages;
import mmrnmhrm.ui.actions.GoToDefinitionAction;
import mmrnmhrm.ui.text.DeeDocument;

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
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.ITextEditor;

import dtool.dom.ast.ASTNode;


/**
 * D AST viewer
 */
public class ASTViewer extends ViewPart implements ISelectionListener,
		IDocumentListener, ISelectionChangedListener, IDoubleClickListener {

	protected TreeViewer viewer;
	private DrillDownAdapter drillDownAdapter;
	private Action actionExpand;
	private Action actionCollapse;

	
	//protected MultiListener fMultiListener;

	protected ITextEditor fEditor;
	protected DeeDocument fDeeDocument;
	protected CompilationUnit fCUnit;
	private Action actionToggle;
	protected boolean fUseOldAst = false;


	public ASTViewer() {
	}
	
		
	@Override
	public void init(IViewSite site) throws PartInitException {
		super.init(site);
		
		ISelectionService service= site.getWorkbenchWindow().getSelectionService();
		service.addPostSelectionListener(this);
		//site.getPage().addPartListener(this);

		/*if (fMultiListener == null) {
			fMultiListener = this.new MultiListener();
			
			ISelectionService service= site.getWorkbenchWindow().getSelectionService();
			service.addPostSelectionListener(fMultiListener);
			site.getPage().addPartListener(fMultiListener);
			//FileBuffers.getTextFileBufferManager().addFileBufferListener(fSuperListener);
		}*/
	}

	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		drillDownAdapter = new DrillDownAdapter(viewer);
		viewer.setContentProvider(new ASTViewerContentProvider(this));
		viewer.setLabelProvider(new ASTViewerLabelProvider());
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
		updateViewer();
	}

	public void setInput(ITextEditor editor) {
		if (fEditor != null && fDeeDocument != null) {
			fDeeDocument.removeDocumentListener(this);
		}
		
		fEditor = null;
		fDeeDocument = null;
		
		if (editor == null) {
			setContentDescription("No Editor available");
			viewer.getControl().setVisible(false);
		} else {
			fEditor = editor;
			IDocument document = fEditor.getDocumentProvider().getDocument(editor.getEditorInput());
			if(document instanceof DeeDocument) {
				fDeeDocument = (DeeDocument) document;
				fDeeDocument.addDocumentListener(this);
				fCUnit = fDeeDocument.getCompilationUnit();
				updateViewer();
			} else {
				setContentDescription("No DeeDocument available");
				viewer.getControl().setVisible(false);
				fCUnit = null;
			}
		}
		
	}


	private void updateViewer() {
		if(fCUnit.parseStatus == EModelStatus.OK) {
			setContentDescription("");
			viewer.getControl().setVisible(true);
			viewer.getControl().setRedraw(false);
			if(fUseOldAst == true)
				viewer.setInput(this.fCUnit.getOldModule());
			else
				viewer.setInput(this.fCUnit.getNeoModule());
			viewer.refresh();
			viewer.getControl().setRedraw(true);
			return;
		}
		setContentDescription(fCUnit.toStringParseStatus());
		viewer.getControl().setVisible(false);
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
				fUseOldAst  = !fUseOldAst; updateViewer();
			}
		};
		actionToggle.setText("Toggle Neo/Old AST");
		actionToggle.setToolTipText("Toggle Neo/Old AST");
		actionToggle.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
	}



	public void doubleClick(DoubleClickEvent event) {
		ISelection selection = viewer.getSelection();
		ASTNode node = (ASTNode) ((IStructuredSelection)selection).getFirstElement();
		GoToDefinitionAction.execute(getSite().getWorkbenchWindow(), node);
	}

	public void selectionChanged(SelectionChangedEvent event) {
		if(fEditor == null)
			return;
		EditorUtil.selectNodeInEditor((AbstractTextEditor)fEditor, event);
	}
}