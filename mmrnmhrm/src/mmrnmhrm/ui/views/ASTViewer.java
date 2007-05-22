package mmrnmhrm.ui.views;

import mmrnmhrm.ui.editor.EditorUtil;
import mmrnmhrm.ui.text.DeeDocument;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.texteditor.ITextEditor;

import util.log.Logg;
import dtool.project.CompilationUnit;


/**
 * D AST viewer
 */
public class ASTViewer extends ViewPart {

	private class MultiListener implements ISelectionListener, IPartListener {

		public void selectionChanged(IWorkbenchPart part, ISelection selection) {
			selectionChanged0(part, selection);
		}

		public void partActivated(IWorkbenchPart part) {
		}

		public void partBroughtToTop(IWorkbenchPart part) {
		}

		public void partClosed(IWorkbenchPart part) {
		}

		public void partDeactivated(IWorkbenchPart part) {
		}

		public void partOpened(IWorkbenchPart part) {
		}
	}

	protected TreeViewer viewer;
	private DrillDownAdapter drillDownAdapter;
	private Action action1;
	private Action action2;
	private Action doubleClickAction;

	
	protected MultiListener fMultiListener;

	protected ITextEditor fEditor;
	protected DeeDocument fDeeDocument;
	protected CompilationUnit fRoot;


	public ASTViewer() {
	}
	
		
	@Override
	public void init(IViewSite site) throws PartInitException {
		super.init(site);
		if (fMultiListener == null) {
			fMultiListener = this.new MultiListener();
			
			ISelectionService service= site.getWorkbenchWindow().getSelectionService();
			service.addPostSelectionListener(fMultiListener);
			site.getPage().addPartListener(fMultiListener);
			//FileBuffers.getTextFileBufferManager().addFileBufferListener(fSuperListener);
		}
	}
	
	public void selectionChanged0(IWorkbenchPart part, ISelection selection) {
		if(part instanceof ITextEditor) {
			setInput((ITextEditor)part);
		} else if(part == null)
			Logg.main.println("SAMPLEVIEW: got null editor.");

	}
	
	public void setInput(ITextEditor editor) {
		if (fEditor != null) {
			uninstallModificationListener();
		}
		
		fEditor = null;
		fDeeDocument = null;
		fRoot = null;
		
		if (editor != null) {
			fEditor = editor;
			IDocument document = fEditor.getDocumentProvider().getDocument(editor.getEditorInput());
			if(document instanceof DeeDocument) {
				fDeeDocument = (DeeDocument) document;
				fRoot = fDeeDocument.getCompilationUnit();
				installModificationListener();
				setContentDescription("");
			} else {
				setContentDescription("No DeeDocument available");
			}
		} else {
			setContentDescription("No D Editor available");
		}
		
		updateView();
	}
	
	private void installModificationListener() {
		//fCurrentDocument.addDocumentListener(fMultiListener);
	}
	
	private void uninstallModificationListener() {
		if (fDeeDocument != null) {
			//fCurrentDocument.removeDocumentListener(fMultiListener);
			fDeeDocument= null;
		}
	}


	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		drillDownAdapter = new DrillDownAdapter(viewer);
		viewer.setContentProvider(new ASTViewerContentProvider(this));
		viewer.setLabelProvider(new ASTViewerLabelProvider());
		viewer.setInput(getViewSite());
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
		
		setContentDescription("My Content Description");
		
		IEditorPart part = EditorUtil.getActiveEditor();
		if (part instanceof ITextEditor) {
			setInput((ITextEditor) part);
		}
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

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(action1);
		manager.add(new Separator());
		manager.add(action2);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(action1);
		manager.add(action2);
		manager.add(new Separator());
		drillDownAdapter.addNavigationActions(manager);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(action1);
		manager.add(action2);
		manager.add(new Separator());
		drillDownAdapter.addNavigationActions(manager);
	}

	private void makeActions() {
		action1 = new Action() {
			public void run() {
				showMessage("Action 1 executed");
			}
		};
		action1.setText("Action 1");
		action1.setToolTipText("Action 1 tooltip");
		action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		
		action2 = new Action() {
			public void run() {
				showMessage("Action 2 executed");
			}
		};
		action2.setText("Action 2");
		action2.setToolTipText("Action 2 tooltip");
		action2.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		doubleClickAction = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection)selection).getFirstElement();
				showMessage("Double-click detected on "+obj.toString());
			}
		};
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}
	
	private void showMessage(String message) {
		MessageDialog.openInformation(
			viewer.getControl().getShell(),
			"D AST Viewer (Mmrnmhrm)",
			message);
	}

	
	
	public void updateView() {
		viewer.getControl().setRedraw(false);
		viewer.refresh();
		viewer.expandAll();
		viewer.getControl().setRedraw(true);
	}
	
	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}