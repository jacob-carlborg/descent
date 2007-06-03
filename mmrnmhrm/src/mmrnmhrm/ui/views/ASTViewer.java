package mmrnmhrm.ui.views;

import melnorme.lang.ui.EditorUtil;
import mmrnmhrm.core.model.CompilationUnit;
import mmrnmhrm.core.model.EModelStatus;
import mmrnmhrm.ui.actions.GoToDefinitionOperation;
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
import org.eclipse.jface.viewers.IStructuredSelection;
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
import org.eclipse.ui.texteditor.ITextEditor;


import dtool.dom.ast.ASTNode;


/**
 * D AST viewer
 */
public class ASTViewer extends ViewPart implements ISelectionListener,
		IDocumentListener {

	protected TreeViewer viewer;
	private DrillDownAdapter drillDownAdapter;
	private Action action1;
	private Action action2;

	
	//protected MultiListener fMultiListener;

	protected ITextEditor fEditor;
	protected DeeDocument fDeeDocument;
	protected CompilationUnit fCUnit;


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
		makeActions();
		contributeToActionBars();
		hookContextMenu();
		hookDoubleClickAction();
		
		selectionChanged(EditorUtil.getActiveEditor(), null);
	}
	
	
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		IWorkbenchPage page = getSite().getWorkbenchWindow().getActivePage();
		if(page == null)
			return;
		
		part = page.getActiveEditor();
		
		if(part instanceof ITextEditor) {
			setInput((ITextEditor)part);
		} else { 
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
			viewer.setInput(this.fCUnit.getModule());
			viewer.getControl().setRedraw(false);
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

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				ASTViewer.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}
	
	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(action1);
		manager.add(action2);
		manager.add(new Separator());
		drillDownAdapter.addNavigationActions(manager);
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(action1);
		manager.add(action2);
		manager.add(new Separator());
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(action1);
		manager.add(action2);
		manager.add(new Separator());
		drillDownAdapter.addNavigationActions(manager);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}


	private void makeActions() {
		action1 = new Action() {
			public void run() {
				viewer.expandAll();
			}
		};
		action1.setText("Expand All");
		action1.setToolTipText("Expand all nodes");
		action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		
		action2 = new Action() {
			public void run() {
				viewer.collapseAll();
			}
		};
		action2.setText("Collapse All");
		action2.setToolTipText("Collapse All nodes");
		action2.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				handleDoubleClick();
			}
		});
	}
	

	private void handleDoubleClick() {
		ISelection selection = viewer.getSelection();
		ASTNode node = (ASTNode) ((IStructuredSelection)selection).getFirstElement();
		GoToDefinitionOperation.execute(getSite().getWorkbenchWindow(), node);
	}
}