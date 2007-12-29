/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package descent.astview.views;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.IFileBuffer;
import org.eclipse.core.filebuffers.IFileBufferListener;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.IShowInSource;
import org.eclipse.ui.part.ShowInContext;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.IWorkbenchActionDefinitionIds;

import descent.astview.ASTViewImages;
import descent.astview.ASTViewPlugin;
import descent.astview.EditorUtility;
import descent.astview.NodeFinder;
import descent.core.IClassFile;
import descent.core.ICompilationUnit;
import descent.core.IJavaElement;
import descent.core.IJavaProject;
import descent.core.IOpenable;
import descent.core.IPackageFragment;
import descent.core.IProblemRequestor;
import descent.core.JavaCore;
import descent.core.JavaModelException;
import descent.core.WorkingCopyOwner;
import descent.core.compiler.IProblem;
import descent.core.dom.AST;
import descent.core.dom.ASTNode;
import descent.core.dom.ASTParser;
import descent.core.dom.CompilationUnit;
import descent.core.dom.IBinding;
import descent.internal.ui.JavaPlugin;
import descent.internal.ui.javaeditor.ASTProvider;
import descent.ui.JavaUI;

public class ASTView extends ViewPart implements IShowInSource {
	
	private static final int D1= AST.D1;
	private static final int D2= AST.D2;

	private class ASTViewSelectionProvider implements ISelectionProvider {
		ListenerList fListeners= new ListenerList(ListenerList.IDENTITY);

		public void addSelectionChangedListener(ISelectionChangedListener listener) {
			fListeners.add(listener);
		}

		public ISelection getSelection() {
			IStructuredSelection selection= (IStructuredSelection) fViewer.getSelection();
			ArrayList<IJavaElement> externalSelection= new ArrayList<IJavaElement>();
			for (Iterator iter= selection.iterator(); iter.hasNext();) {
				Object element= iter.next();
				if (element instanceof JavaElement)
					externalSelection.add(((JavaElement) element).getJavaElement());
			}
			return new StructuredSelection(externalSelection);
		}

		public void removeSelectionChangedListener(ISelectionChangedListener listener) {
			fListeners.remove(listener);
		}

		public void setSelection(ISelection selection) {
			//not supported
		}
	}

	private class ASTLevelToggle extends Action {
		private int fLevel;

		public ASTLevelToggle(String label, int level) {
			super(label, AS_RADIO_BUTTON);
			fLevel= level;
			if (level == getCurrentASTLevel()) {
				setChecked(true);
			}
		}
	
		public int getLevel() {
			return fLevel;
		}
		
		public void run() {
			setASTLevel(fLevel, true);
		}
	}
	
	private class ASTInputKindAction extends Action {
		public static final int USE_PARSER= 1;
		
		private int fInputKind;

		public ASTInputKindAction(String label, int inputKind) {
			super(label, AS_RADIO_BUTTON);
			fInputKind= inputKind;
			if (inputKind == getCurrentInputKind()) {
				setChecked(true);
			}
		}
	
		public int getInputKind() {
			return fInputKind;
		}
		
		public void run() {
			setASTInputType(fInputKind);
		}
	}
	
	
	private static class ListenerMix implements ISelectionListener, IFileBufferListener, IDocumentListener, ISelectionChangedListener, IDoubleClickListener, IPartListener2 {
		
		private boolean fASTViewVisible= true;
		private ASTView fView;
		
		public ListenerMix(ASTView view) {
			fView= view;
		}
		
		public void dispose() {
			fView= null;
		}

		public void selectionChanged(IWorkbenchPart part, ISelection selection) {
			if (fASTViewVisible) {
				fView.handleEditorPostSelectionChanged(part, selection);
			}
		}

		/* (non-Javadoc)
		 * @see org.eclipse.core.filebuffers.IFileBufferListener#bufferCreated(org.eclipse.core.filebuffers.IFileBuffer)
		 */
		public void bufferCreated(IFileBuffer buffer) {
			// not interesting
		}

		/* (non-Javadoc)
		 * @see org.eclipse.core.filebuffers.IFileBufferListener#bufferDisposed(org.eclipse.core.filebuffers.IFileBuffer)
		 */
		public void bufferDisposed(IFileBuffer buffer) {
			if (buffer instanceof ITextFileBuffer) {
				fView.handleDocumentDisposed(((ITextFileBuffer) buffer).getDocument());
			}
		}

		/* (non-Javadoc)
		 * @see org.eclipse.core.filebuffers.IFileBufferListener#bufferContentAboutToBeReplaced(org.eclipse.core.filebuffers.IFileBuffer)
		 */
		public void bufferContentAboutToBeReplaced(IFileBuffer buffer) {
			// not interesting
		}

		/* (non-Javadoc)
		 * @see org.eclipse.core.filebuffers.IFileBufferListener#bufferContentReplaced(org.eclipse.core.filebuffers.IFileBuffer)
		 */
		public void bufferContentReplaced(IFileBuffer buffer) {
			// not interesting
		}

		/* (non-Javadoc)
		 * @see org.eclipse.core.filebuffers.IFileBufferListener#stateChanging(org.eclipse.core.filebuffers.IFileBuffer)
		 */
		public void stateChanging(IFileBuffer buffer) {
			// not interesting
		}

		/* (non-Javadoc)
		 * @see org.eclipse.core.filebuffers.IFileBufferListener#dirtyStateChanged(org.eclipse.core.filebuffers.IFileBuffer, boolean)
		 */
		public void dirtyStateChanged(IFileBuffer buffer, boolean isDirty) {
			// not interesting
		}

		/* (non-Javadoc)
		 * @see org.eclipse.core.filebuffers.IFileBufferListener#stateValidationChanged(org.eclipse.core.filebuffers.IFileBuffer, boolean)
		 */
		public void stateValidationChanged(IFileBuffer buffer, boolean isStateValidated) {
			// not interesting
		}

		/* (non-Javadoc)
		 * @see org.eclipse.core.filebuffers.IFileBufferListener#underlyingFileMoved(org.eclipse.core.filebuffers.IFileBuffer, org.eclipse.core.runtime.IPath)
		 */
		public void underlyingFileMoved(IFileBuffer buffer, IPath path) {
			// not interesting
		}

		/* (non-Javadoc)
		 * @see org.eclipse.core.filebuffers.IFileBufferListener#underlyingFileDeleted(org.eclipse.core.filebuffers.IFileBuffer)
		 */
		public void underlyingFileDeleted(IFileBuffer buffer) {
			// not interesting
		}

		/* (non-Javadoc)
		 * @see org.eclipse.core.filebuffers.IFileBufferListener#stateChangeFailed(org.eclipse.core.filebuffers.IFileBuffer)
		 */
		public void stateChangeFailed(IFileBuffer buffer) {
			// not interesting
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.text.IDocumentListener#documentAboutToBeChanged(org.eclipse.jface.text.DocumentEvent)
		 */
		public void documentAboutToBeChanged(DocumentEvent event) {
			// not interesting
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.text.IDocumentListener#documentChanged(org.eclipse.jface.text.DocumentEvent)
		 */
		public void documentChanged(DocumentEvent event) {
			fView.handleDocumentChanged(event.getDocument());
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
		 */
		public void selectionChanged(SelectionChangedEvent event) {
			fView.handleSelectionChanged(event.getSelection());
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IDoubleClickListener#doubleClick(org.eclipse.jface.viewers.DoubleClickEvent)
		 */
		public void doubleClick(DoubleClickEvent event) {
			fView.handleDoubleClick(event);
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.ui.IPartListener2#partHidden(org.eclipse.ui.IWorkbenchPartReference)
		 */
		public void partHidden(IWorkbenchPartReference partRef) {
			IWorkbenchPart part= partRef.getPart(false);
			if (part == fView) {
				fASTViewVisible= false;
			}
		}

		/* (non-Javadoc)
		 * @see org.eclipse.ui.IPartListener2#partVisible(org.eclipse.ui.IWorkbenchPartReference)
		 */
		public void partVisible(IWorkbenchPartReference partRef) {
			IWorkbenchPart part= partRef.getPart(false);
			if (part == fView) {
				fASTViewVisible= true;
			}
		}

		/* (non-Javadoc)
		 * @see org.eclipse.ui.IPartListener2#partActivated(org.eclipse.ui.IWorkbenchPartReference)
		 */
		public void partActivated(IWorkbenchPartReference partRef) {
			// not interesting
		}

		/* (non-Javadoc)
		 * @see org.eclipse.ui.IPartListener2#partBroughtToTop(org.eclipse.ui.IWorkbenchPartReference)
		 */
		public void partBroughtToTop(IWorkbenchPartReference partRef) {
			// not interesting
		}

		/* (non-Javadoc)
		 * @see org.eclipse.ui.IPartListener2#partClosed(org.eclipse.ui.IWorkbenchPartReference)
		 */
		public void partClosed(IWorkbenchPartReference partRef) {
			fView.notifyWorkbenchPartClosed(partRef);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.ui.IPartListener2#partDeactivated(org.eclipse.ui.IWorkbenchPartReference)
		 */
		public void partDeactivated(IWorkbenchPartReference partRef) {
			// not interesting
		}

		/* (non-Javadoc)
		 * @see org.eclipse.ui.IPartListener2#partOpened(org.eclipse.ui.IWorkbenchPartReference)
		 */
		public void partOpened(IWorkbenchPartReference partRef) {
			// not interesting
		}

		/* (non-Javadoc)
		 * @see org.eclipse.ui.IPartListener2#partInputChanged(org.eclipse.ui.IWorkbenchPartReference)
		 */
		public void partInputChanged(IWorkbenchPartReference partRef) {
			// not interesting
		}
	}
		
	private final static String SETTINGS_LINK_WITH_EDITOR= "link_with_editor"; //$NON-NLS-1$
	private final static String SETTINGS_INPUT_KIND= "input_kind"; //$NON-NLS-1$
	private final static String SETTINGS_NO_BINDINGS= "create_bindings"; //$NON-NLS-1$
	private final static String SETTINGS_NO_STATEMENTS_RECOVERY= "no_statements_recovery"; //$NON-NLS-1$
	private final static String SETTINGS_SHOW_NON_RELEVANT="show_non_relevant";//$NON-NLS-1$
	private final static String SETTINGS_JLS= "jls"; //$NON-NLS-1$

	
	private SashForm fSash;
	private TreeViewer fViewer;
	private ASTViewLabelProvider fASTLabelProvider;
	private TreeViewer fTray;
	
	private DrillDownAdapter fDrillDownAdapter;
	private Action fFocusAction;
	private Action fRefreshAction;
	private Action fCreateBindingsAction;
	private Action fStatementsRecoveryAction;
	private Action fFilterNonRelevantAction;
	private Action fResolveBindingKeyAction;
	private Action fCreateBindingFromElementAction;
	private Action fCollapseAction;
	private Action fExpandAction;
	private Action fClearAction;
	private TreeCopyAction fCopyAction;
	private Action fDoubleClickAction;
	private Action fLinkWithEditor;
	private Action fAddToTrayAction;
	private Action fDeleteAction;
	
	private ASTLevelToggle[] fASTVersionToggleActions;
	private int fCurrentASTLevel;
	
	private ASTInputKindAction[] fASTInputKindActions;
	private int fCurrentInputKind;
	
	private ITextEditor fEditor;
	private IOpenable fOpenable;
	private CompilationUnit fRoot;
	private IDocument fCurrentDocument;
	private ArrayList fTrayRoots;
	
	private boolean fDoLinkWithEditor;
	private boolean fCreateBindings;
	private NonRelevantFilter fNonRelevantFilter;
	private boolean fStatementsRecovery;
	
	private Object fPreviousDouble;
	
	private ListenerMix fSuperListener;
	private ISelectionChangedListener fTrayUpdater;

	private IDialogSettings fDialogSettings;

	
	public ASTView() {
		fSuperListener= null;
		fDialogSettings= ASTViewPlugin.getDefault().getDialogSettings();
		fDoLinkWithEditor= fDialogSettings.getBoolean(SETTINGS_LINK_WITH_EDITOR);
		try {
			fCurrentInputKind= fDialogSettings.getInt(SETTINGS_INPUT_KIND);
		} catch (NumberFormatException e) {
			fCurrentInputKind= ASTInputKindAction.USE_PARSER;
		}
		fCreateBindings= !fDialogSettings.getBoolean(SETTINGS_NO_BINDINGS); // inverse so that default is to create bindings
		fStatementsRecovery= !fDialogSettings.getBoolean(SETTINGS_NO_STATEMENTS_RECOVERY); // inverse so that default is use recovery
		fCurrentASTLevel= D1;
		try {
			int level= fDialogSettings.getInt(SETTINGS_JLS);
			if (level == D1) {
				fCurrentASTLevel= level;
			}
		} catch (NumberFormatException e) {
			// ignore
		}
		fNonRelevantFilter= new NonRelevantFilter();
		fNonRelevantFilter.setShowNonRelevant(fDialogSettings.getBoolean(SETTINGS_SHOW_NON_RELEVANT));
	}
	
	final void notifyWorkbenchPartClosed(IWorkbenchPartReference partRef) {
		if (fEditor != null && fEditor.equals(partRef.getPart(false))) {
			try {
				setInput(null);
			} catch (CoreException e) {
				// ignore
			}
		}
	}

	/*(non-Javadoc)
	 * @see org.eclipse.ui.IViewPart#init(org.eclipse.ui.IViewSite)
	 */
	public void init(IViewSite site) throws PartInitException {
		super.setSite(site);
		if (fSuperListener == null) {
			fSuperListener= new ListenerMix(this);
			
			ISelectionService service= site.getWorkbenchWindow().getSelectionService();
			service.addPostSelectionListener(fSuperListener);
			site.getPage().addPartListener(fSuperListener);
			FileBuffers.getTextFileBufferManager().addFileBufferListener(fSuperListener);
		}
	}
	
	public int getCurrentASTLevel() {
		return fCurrentASTLevel;
	}
	
	public int getCurrentInputKind() {
		return fCurrentInputKind;
	}

	public void setInput(ITextEditor editor) throws CoreException {
		if (fEditor != null) {
			uninstallModificationListener();
		}
		
		fEditor= null;
		fRoot= null;
		
		if (editor != null) {
			IOpenable openable= EditorUtility.getJavaInput(editor);
			if (openable == null) {
				throw new CoreException(getErrorStatus("Editor not showing a CU or class file", null)); //$NON-NLS-1$
			}
			fOpenable= openable;
			int astLevel= getInitialASTLevel((IJavaElement) openable);
			
			ISelection selection= editor.getSelectionProvider().getSelection();
			if (selection instanceof ITextSelection) {
				ITextSelection textSelection= (ITextSelection) selection;
				fRoot= internalSetInput(openable, textSelection.getOffset(), textSelection.getLength(), astLevel);
				fEditor= editor;
				setASTLevel(astLevel, false);
			}
			installModificationListener();
		}

	}
	
	private int getInitialASTLevel(IJavaElement openable) {
		IJavaProject project= (IJavaProject) openable.getAncestor(IJavaElement.JAVA_PROJECT);
		String option= project.getOption(JavaCore.COMPILER_SOURCE, true);
		if (JavaCore.VERSION_2_x.equals(option)) {
			return D2;
		} else {
			return D1;
		}
	}

	private CompilationUnit internalSetInput(IOpenable input, int offset, int length, int astLevel) throws CoreException {
		if (input.getBuffer() == null) {
			throw new CoreException(getErrorStatus("Input has no buffer", null)); //$NON-NLS-1$
		}
		
		try {
			CompilationUnit root= createAST(input, astLevel, offset);
			resetView(root);
			if (root == null) {
				setContentDescription("AST could not be created."); //$NON-NLS-1$
				return null;
			}
			ASTNode node= NodeFinder.perform(root, offset, length);
			if (node != null) {
				fViewer.getTree().setRedraw(false);
				fASTLabelProvider.setSelectedRange(node.getStartPosition(), node.getLength());
				fViewer.setSelection(new StructuredSelection(node), true);
				fViewer.getTree().setRedraw(true);
			}
			return root;
			
		} catch (RuntimeException e) {
			throw new CoreException(getErrorStatus("Could not create AST:\n" + e.getMessage(), e)); //$NON-NLS-1$
		}
	}
	
	private CompilationUnit createAST(IOpenable input, int astLevel, int offset) throws JavaModelException, CoreException {
		long startTime;
		long endTime;
		CompilationUnit root;
		
		if ((input instanceof ICompilationUnit || input instanceof IClassFile)) {
			IProblemRequestor problemRequestor= new IProblemRequestor() { //strange: don't get bindings when supplying null as problemRequestor
				public void acceptProblem(IProblem problem) {/*not interested*/}
				public void beginReporting() {/*not interested*/}
				public void endReporting() {/*not interested*/}
				public boolean isActive() {
					return true;
				}
			};
			ICompilationUnit wc;
			if (input instanceof ICompilationUnit) {
				wc= ((ICompilationUnit) input).getWorkingCopy(
						new WorkingCopyOwner() {/*useless subclass*/},
						problemRequestor,
						null);
			} else {
				wc= ((IClassFile) input).becomeWorkingCopy(
						problemRequestor,
						new WorkingCopyOwner() {/*useless subclass*/},
						null);
			}
			try {
				//make inconsistent (otherwise, no AST is generated):
//				IBuffer buffer= wc.getBuffer();
//				buffer.append(new char[] {' '});
//				buffer.replace(buffer.getLength() - 1, 1, new char[0]);
				
				startTime= System.currentTimeMillis();
				root= wc.reconcile(getCurrentASTLevel(), true, fStatementsRecovery, null, null);
				endTime= System.currentTimeMillis();
			} finally {
				wc.discardWorkingCopy();
			}
			
		} else if (input instanceof ICompilationUnit) {
			ICompilationUnit cu= (ICompilationUnit) input;
			startTime= System.currentTimeMillis();
			root= JavaPlugin.getDefault().getASTProvider().getAST(cu, ASTProvider.WAIT_NO, null);
			endTime= System.currentTimeMillis();
			
		} else {
			ASTParser parser= ASTParser.newParser(astLevel);
			parser.setResolveBindings(fCreateBindings);
			if (input instanceof ICompilationUnit) {
				parser.setSource((ICompilationUnit) input);
			} else {
				//parser.setSource((IClassFile) input);
			}
			parser.setStatementsRecovery(fStatementsRecovery);
			/*
			if (getCurrentInputKind() == ASTInputKindAction.USE_FOCAL) {
				parser.setFocalPosition(offset);
			}
			*/
			startTime= System.currentTimeMillis();
			root= (CompilationUnit) parser.createAST(null);
			endTime= System.currentTimeMillis();
		}
		if (root != null) {
			//updateContentDescription((IJavaElement) input, root, endTime - startTime);
		}
		return root;
	}
	
	private void clearView() {
		resetView(null);
		setContentDescription("Open a D editor and press the 'Show AST of active editor' toolbar button"); //$NON-NLS-1$
	}
	

	private void resetView(CompilationUnit root) {
		fViewer.setInput(root);
		fViewer.getTree().setEnabled(root != null);
		fSash.setMaximizedControl(fViewer.getTree());
		fTrayRoots= new ArrayList();
		if (fTray != null)
			fTray.setInput(fTrayRoots);
		setASTUptoDate(root != null);
		fClearAction.setEnabled(root != null);
		fResolveBindingKeyAction.setEnabled(root != null);
		fPreviousDouble= null; // avoid leaking AST
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPart#dispose()
	 */
	public void dispose() {
		if (fSuperListener != null) {
			if (fEditor != null) {
				uninstallModificationListener();
			}
			ISelectionService service= getSite().getWorkbenchWindow().getSelectionService();
			service.removePostSelectionListener(fSuperListener);
			getSite().getPage().removePartListener(fSuperListener);
			FileBuffers.getTextFileBufferManager().removeFileBufferListener(fSuperListener);
			fSuperListener.dispose(); // removes reference to view
			fSuperListener= null;
		}
		if (fTrayUpdater != null) {
			fViewer.removePostSelectionChangedListener(fTrayUpdater);
			fTray.removePostSelectionChangedListener(fTrayUpdater);
			fTrayUpdater= null;
		}
		super.dispose();
	}
	
	private IStatus getErrorStatus(String message, Throwable th) {
		return new Status(IStatus.ERROR, ASTViewPlugin.getPluginId(), IStatus.ERROR, message, th);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent) {
		fSash= new SashForm(parent, SWT.VERTICAL | SWT.SMOOTH);
		fViewer = new TreeViewer(fSash, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		fDrillDownAdapter = new DrillDownAdapter(fViewer);
		fViewer.setContentProvider(new ASTViewContentProvider());
		fASTLabelProvider= new ASTViewLabelProvider();
		fViewer.setLabelProvider(fASTLabelProvider);
		fViewer.addSelectionChangedListener(fSuperListener);
		fViewer.addDoubleClickListener(fSuperListener);
		fViewer.addFilter(new ViewerFilter() {
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				if (!fCreateBindings && element instanceof Binding)
					return false;
				return true;
			}
		});
		fViewer.addFilter(fNonRelevantFilter);
		
		ViewForm trayForm= new ViewForm(fSash, SWT.NONE);
		Label label= new Label(trayForm, SWT.NONE);
		label.setText(" Comparison Tray (* = selection in the upper tree):"); //$NON-NLS-1$
		trayForm.setTopLeft(label);
		
		fTray= new TreeViewer(trayForm, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		trayForm.setContent(fTray.getTree());
		
		fTrayRoots= new ArrayList();
		fTray.setContentProvider(new TrayContentProvider());
		final TrayLabelProvider trayLabelProvider= new TrayLabelProvider();
		fTray.setLabelProvider(trayLabelProvider);
		fTray.setAutoExpandLevel(AbstractTreeViewer.ALL_LEVELS);
		fTrayUpdater= new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection viewerSelection= (IStructuredSelection) fViewer.getSelection();
				if (viewerSelection.size() == 1) {
					if (DynamicAttributeProperty.unwrapAttribute(viewerSelection.getFirstElement()) != null) {
						trayLabelProvider.setViewerElement(viewerSelection.getFirstElement());
						return;
					}
				}
				trayLabelProvider.setViewerElement(null);
			}
		};
		fTray.addPostSelectionChangedListener(fTrayUpdater);
		fViewer.addPostSelectionChangedListener(fTrayUpdater);
		fTray.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				performTrayDoubleClick();
			}
		});
		fTray.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection= (IStructuredSelection) event.getSelection();
				boolean deleteEnabled= false;
				if (selection.size() == 1 && selection.getFirstElement() instanceof Binding)
					deleteEnabled= fTray.getTree().isFocusControl();
				fDeleteAction.setEnabled(deleteEnabled);
			}
		});
		fTray.getTree().addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				IStructuredSelection selection= (IStructuredSelection) fTray.getSelection();
				boolean deleteEnabled= false;
				if (selection.size() == 1 && selection.getFirstElement() instanceof Binding)
					deleteEnabled= true;
				fDeleteAction.setEnabled(deleteEnabled);
			}
			public void focusLost(FocusEvent e) {
				fDeleteAction.setEnabled(false);
			}
		});
		
		makeActions();
		hookContextMenu();
		hookTrayContextMenu();
		contributeToActionBars();
		getSite().setSelectionProvider(new ASTViewSelectionProvider());
		
		try {
			IEditorPart part= EditorUtility.getActiveEditor();
			if (part instanceof ITextEditor) {
				setInput((ITextEditor) part);
			}
		} catch (CoreException e) {
			// ignore
		}
		if (fOpenable == null) {
			clearView();
		} else {
			setASTUptoDate(fOpenable != null);
		}
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				ASTView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(fViewer.getControl());
		fViewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, fViewer);
	}
	
	private void hookTrayContextMenu() {
		MenuManager menuMgr = new MenuManager("#TrayPopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				manager.add(fCopyAction);
				manager.add(fDeleteAction);
				manager.add(new Separator());
				manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
			}
		});
		Menu menu = menuMgr.createContextMenu(fTray.getControl());
		fTray.getControl().setMenu(menu);
		getSite().registerContextMenu("#TrayPopupMenu", menuMgr, fTray); //$NON-NLS-1$
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
		bars.setGlobalActionHandler(ActionFactory.COPY.getId(), fCopyAction);
		bars.setGlobalActionHandler(ActionFactory.REFRESH.getId(), fFocusAction);
		bars.setGlobalActionHandler(ActionFactory.DELETE.getId(), fDeleteAction);
	}

	private void fillLocalPullDown(IMenuManager manager) {
		for (int i= 0; i < fASTVersionToggleActions.length; i++) {
			manager.add(fASTVersionToggleActions[i]);	
		}
		manager.add(new Separator());
		manager.add(fCreateBindingsAction);
		manager.add(fStatementsRecoveryAction);
		manager.add(fFilterNonRelevantAction);
		manager.add(new Separator());
		manager.add(fResolveBindingKeyAction);
		manager.add(fCreateBindingFromElementAction);
		manager.add(new Separator());
		manager.add(fLinkWithEditor);
		
		MenuManager menu= new MenuManager("&Advanced Options");
		manager.add(menu);
		for (int i= 0; i < fASTInputKindActions.length; i++) {
			menu.add(fASTInputKindActions[i]);	
		}
	}

	protected void fillContextMenu(IMenuManager manager) {
		manager.add(fFocusAction);
		manager.add(fRefreshAction);
		manager.add(fClearAction);
		manager.add(fCollapseAction);
		manager.add(fExpandAction);
		manager.add(new Separator());
		manager.add(fCopyAction);
		if (fAddToTrayAction.isEnabled())
			manager.add(fAddToTrayAction);
		manager.add(new Separator());

		fDrillDownAdapter.addNavigationActions(manager);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(fFocusAction);
		manager.add(fRefreshAction);
		manager.add(fClearAction);
		manager.add(new Separator());
		fDrillDownAdapter.addNavigationActions(manager);
		manager.add(new Separator());
		manager.add(fExpandAction);
		manager.add(fCollapseAction);
		manager.add(fLinkWithEditor);
	}
	
	private void setASTUptoDate(boolean isuptoDate) {
		fRefreshAction.setEnabled(!isuptoDate && fOpenable != null);
	}
	
	private void makeActions() {
		fRefreshAction = new Action() {
			public void run() {
				performRefresh();
			}
		};
		fRefreshAction.setText("&Refresh AST"); //$NON-NLS-1$
		fRefreshAction.setToolTipText("Refresh AST"); //$NON-NLS-1$
		fRefreshAction.setEnabled(false);
		ASTViewImages.setImageDescriptors(fRefreshAction, ASTViewImages.REFRESH);

		fClearAction = new Action() {
			public void run() {
				performClear();
			}
		};
		fClearAction.setText("&Clear AST"); //$NON-NLS-1$
		fClearAction.setToolTipText("Clear AST and release memory"); //$NON-NLS-1$
		fClearAction.setEnabled(false);
		ASTViewImages.setImageDescriptors(fClearAction, ASTViewImages.CLEAR);
				
		fASTInputKindActions= new ASTInputKindAction[] {
				new ASTInputKindAction("Use ASTParser.&createAST", ASTInputKindAction.USE_PARSER), //$NON-NLS-1$
		};
		
		fCreateBindingsAction = new Action("&Create Bindings", IAction.AS_CHECK_BOX) { //$NON-NLS-1$
			public void run() {
				performCreateBindings();
			}
		};
		fCreateBindingsAction.setChecked(fCreateBindings);
		fCreateBindingsAction.setToolTipText("Create Bindings"); //$NON-NLS-1$
		fCreateBindingsAction.setEnabled(true);
		
		fStatementsRecoveryAction = new Action("&Statements Recovery", IAction.AS_CHECK_BOX) { //$NON-NLS-1$
			public void run() {
				performStatementsRecovery();
			}
		};
		fStatementsRecoveryAction.setChecked(fStatementsRecovery);
		fStatementsRecoveryAction.setEnabled(true);
		
		fFilterNonRelevantAction = new Action("&Hide Non-Relevant Attributes", IAction.AS_CHECK_BOX) { //$NON-NLS-1$
			public void run() {
				performFilterNonRelevant();
			}
		};
		fFilterNonRelevantAction.setChecked(! fNonRelevantFilter.isShowNonRelevant());
		fFilterNonRelevantAction.setToolTipText("Hide non-relevant binding attributes"); //$NON-NLS-1$
		fFilterNonRelevantAction.setEnabled(true);

		fResolveBindingKeyAction= new Action("&Resolve Binding Key...", IAction.AS_PUSH_BUTTON) { //$NON-NLS-1$
			public void run() {
				performResolveBindingKey();
			}
		};
		fResolveBindingKeyAction.setToolTipText("Resolve Binding Key..."); //$NON-NLS-1$
		fResolveBindingKeyAction.setEnabled(false);
		
		fCreateBindingFromElementAction= new Action("&Create Binding from Element Handle...", IAction.AS_PUSH_BUTTON) { //$NON-NLS-1$
			public void run() {
				performCreateBindingFromElement();
			}
		};
		fCreateBindingFromElementAction.setToolTipText("Create Binding from Element Handle..."); //$NON-NLS-1$
		fCreateBindingFromElementAction.setEnabled(true);
		
		fFocusAction = new Action() {
			public void run() {
				performSetFocus();
			}
		};
		fFocusAction.setText("&Show AST of active editor"); //$NON-NLS-1$
		fFocusAction.setToolTipText("Show AST of active editor"); //$NON-NLS-1$
		fFocusAction.setActionDefinitionId("org.eclipse.ui.file.refresh"); //$NON-NLS-1$
		ASTViewImages.setImageDescriptors(fFocusAction, ASTViewImages.SETFOCUS);

		fCollapseAction = new Action() {
			public void run() {
				performCollapse();
			}
		};
		fCollapseAction.setText("C&ollapse"); //$NON-NLS-1$
		fCollapseAction.setToolTipText("Collapse Selected Node"); //$NON-NLS-1$
		fCollapseAction.setEnabled(false);
		ASTViewImages.setImageDescriptors(fCollapseAction, ASTViewImages.COLLAPSE);
		
		fExpandAction = new Action() {
			public void run() {
				performExpand();
			}
		};
		fExpandAction.setText("E&xpand"); //$NON-NLS-1$
		fExpandAction.setToolTipText("Expand Selected Node"); //$NON-NLS-1$
		fExpandAction.setEnabled(false);
		ASTViewImages.setImageDescriptors(fExpandAction, ASTViewImages.EXPAND);
		
		fCopyAction= new TreeCopyAction(new Tree[] { fViewer.getTree() });
		
		fDoubleClickAction = new Action() {
			public void run() {
				performDoubleClick();
			}
		};
		
		fLinkWithEditor = new Action() {
			public void run() {
				performLinkWithEditor();
			}
		};
		fLinkWithEditor.setChecked(fDoLinkWithEditor);
		fLinkWithEditor.setText("&Link with Editor"); //$NON-NLS-1$
		fLinkWithEditor.setToolTipText("Link With Editor"); //$NON-NLS-1$
		ASTViewImages.setImageDescriptors(fLinkWithEditor, ASTViewImages.LINK_WITH_EDITOR);
			
		fASTVersionToggleActions= new ASTLevelToggle[] {
				//new ASTLevelToggle("AST Level &2.0", JLS2), //$NON-NLS-1$
				new ASTLevelToggle("AST Level &1.0", D1), //$NON-NLS-1$
				new ASTLevelToggle("AST Level &2.0", D2) //$NON-NLS-1$
		};
		
		fAddToTrayAction= new Action() {
			public void run() {
				performAddToTray();
			}
		};
		fAddToTrayAction.setText("&Add to Comparison Tray"); //$NON-NLS-1$
		fAddToTrayAction.setToolTipText("Add Selected Node to Comparison Tray"); //$NON-NLS-1$
		fAddToTrayAction.setEnabled(false);
		ASTViewImages.setImageDescriptors(fAddToTrayAction, ASTViewImages.ADD_TO_TRAY);
		
		fDeleteAction= new Action() {
			public void run() {
				performDelete();
			}
		};
		fDeleteAction.setText("&Delete"); //$NON-NLS-1$
		fDeleteAction.setToolTipText("Delete Binding from Tray"); //$NON-NLS-1$
		fDeleteAction.setEnabled(false);
		fDeleteAction.setImageDescriptor(ASTViewPlugin.getDefault().getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
		fDeleteAction.setId(ActionFactory.DELETE.getId());
		fDeleteAction.setActionDefinitionId(IWorkbenchActionDefinitionIds.DELETE);
	}
	


	private void refreshAST() throws CoreException {
		ASTNode node= getASTNodeNearSelection((IStructuredSelection) fViewer.getSelection());
		int offset= 0;
		int length= 0;
		if (node != null) {
			offset= node.getStartPosition();
			length= node.getLength();
		}

		internalSetInput(fOpenable, offset, length, getCurrentASTLevel());
	}
		
	protected void setASTLevel(int level, boolean doRefresh) {
		int oldLevel= fCurrentASTLevel;
		fCurrentASTLevel= level;

		fDialogSettings.put(SETTINGS_JLS, fCurrentASTLevel);
		
		if (doRefresh && fOpenable != null && oldLevel != fCurrentASTLevel) {
			try {
				refreshAST();
			} catch (CoreException e) {
				showAndLogError("Could not set AST to new level.", e); //$NON-NLS-1$
				// set back to old level
				fCurrentASTLevel= oldLevel;
			}
		}
		// update action state
		for (int i= 0; i < fASTVersionToggleActions.length; i++) {
			ASTLevelToggle curr= fASTVersionToggleActions[i];
			curr.setChecked(curr.getLevel() == fCurrentASTLevel);
		}
	}
	
	protected void setASTInputType(int inputKind) {
		if (inputKind != fCurrentInputKind) {
			fCurrentInputKind= inputKind;
			fDialogSettings.put(SETTINGS_INPUT_KIND, inputKind);
			for (int i= 0; i < fASTInputKindActions.length; i++) {
				ASTInputKindAction curr= fASTInputKindActions[i];
				curr.setChecked(curr.getInputKind() == inputKind);
			}
			performRefresh();
		}
	}
	
	private ASTNode getASTNodeNearSelection(IStructuredSelection selection) {
		Object elem= selection.getFirstElement();
		if (elem instanceof ASTAttribute) {
			return ((ASTAttribute) elem).getParentASTNode();
		} else if (elem instanceof ASTNode) {
			return (ASTNode) elem;
		}
		return null;
	}
	
	private void installModificationListener() {
		fCurrentDocument= fEditor.getDocumentProvider().getDocument(fEditor.getEditorInput());
		fCurrentDocument.addDocumentListener(fSuperListener);
	}
	
	private void uninstallModificationListener() {
		if (fCurrentDocument != null) {
			fCurrentDocument.removeDocumentListener(fSuperListener);
			fCurrentDocument= null;
		}
	}
		
	protected void handleDocumentDisposed(IDocument document) {
		uninstallModificationListener();
	}
	
	protected void handleDocumentChanged(IDocument document) {
		setASTUptoDate(false);
	}
	
	protected void handleSelectionChanged(ISelection selection) {
		fExpandAction.setEnabled(!selection.isEmpty());
		fCollapseAction.setEnabled(!selection.isEmpty());
		fCopyAction.setEnabled(!selection.isEmpty());
		
		boolean addEnabled= false;
		IStructuredSelection structuredSelection= (IStructuredSelection) selection;
		if (structuredSelection.size() == 1 && fViewer.getTree().isFocusControl()) {
			Object first= structuredSelection.getFirstElement();
			Object unwrapped= DynamicAttributeProperty.unwrapAttribute(first);
			addEnabled= unwrapped != null;
		}
		fAddToTrayAction.setEnabled(addEnabled);

	}

	protected void handleEditorPostSelectionChanged(IWorkbenchPart part, ISelection selection) {
		if (!(selection instanceof ITextSelection)) {
			return;
		}
		ITextSelection textSelection= (ITextSelection) selection;
		fASTLabelProvider.setSelectedRange(textSelection.getOffset(), textSelection.getLength());
		if (!fDoLinkWithEditor) {
			return;
		}
		if (fRoot == null || part != fEditor) {
			if (part instanceof ITextEditor && (EditorUtility.getJavaInput((ITextEditor) part) != null)) {
				try {
					setInput((ITextEditor) part);
				} catch (CoreException e) {
					setContentDescription(e.getStatus().getMessage());
				}
			}
			
		} else { // fRoot != null && part == fEditor
			doLinkWithEditor(selection);
		}
	}
	
	private void doLinkWithEditor(ISelection selection) {
		ITextSelection textSelection= (ITextSelection) selection;
		int offset= textSelection.getOffset();
		int length= textSelection.getLength();
		
		NodeFinder finder= new NodeFinder(offset, length);
		fRoot.accept(finder);
		ASTNode covering= finder.getCoveringNode();
		if (covering != null) {
			fViewer.reveal(covering);
			fViewer.setSelection(new StructuredSelection(covering));
		}
	}

	protected void handleDoubleClick(DoubleClickEvent event) {
		fDoubleClickAction.run();
	}

	protected void performLinkWithEditor() {
		fDoLinkWithEditor= fLinkWithEditor.isChecked();
		fDialogSettings.put(SETTINGS_LINK_WITH_EDITOR, fDoLinkWithEditor);
		

		if (fDoLinkWithEditor && fEditor != null) {
			ISelectionProvider selectionProvider= fEditor.getSelectionProvider();
			if (selectionProvider != null) { // can be null when editor is closed
				doLinkWithEditor(selectionProvider.getSelection());
			}
		}
	}

	protected void performCollapse() {
		IStructuredSelection selection= (IStructuredSelection) fViewer.getSelection();
		if (selection.isEmpty()) {
			fViewer.collapseAll();
		} else {
			Object[] selected= selection.toArray();
			fViewer.getTree().setRedraw(false);
			for (int i= 0; i < selected.length; i++) {
				fViewer.collapseToLevel(selected[i], AbstractTreeViewer.ALL_LEVELS);
			}
			fViewer.getTree().setRedraw(true);
		}
	}

	protected void performExpand() {	
		IStructuredSelection selection= (IStructuredSelection) fViewer.getSelection();
		if (selection.isEmpty()) {
			fViewer.expandToLevel(3);
		} else {
			Object[] selected= selection.toArray();
			fViewer.getTree().setRedraw(false);
			for (int i= 0; i < selected.length; i++) {
				fViewer.expandToLevel(selected[i], 3);
			}
			fViewer.getTree().setRedraw(true);
		}
	}

	protected void performSetFocus() {
		IEditorPart part= EditorUtility.getActiveEditor();
		if (part instanceof ITextEditor) {
			try {
				setInput((ITextEditor) part);
			} catch (CoreException e) {
				showAndLogError("Could not set AST view input ", e); //$NON-NLS-1$
			}
		}
	}
	
	protected void performRefresh() {
		if (fOpenable != null) {
			try {
				refreshAST();
			} catch (CoreException e) {
				showAndLogError("Could not set AST view input ", e); //$NON-NLS-1$
			}
		}
	}

	protected void performClear() {
		fOpenable= null;
		try {
			setInput(null);
		} catch (CoreException e) {
			showAndLogError("Could not reset AST view ", e); //$NON-NLS-1$
		}
		clearView();
	}

	private void showAndLogError(String message, CoreException e) {
		ASTViewPlugin.log(message, e);
		ErrorDialog.openError(getSite().getShell(), "AST View", message, e.getStatus()); //$NON-NLS-1$
	}
	
	private void showAndLogError(String message, Exception e) {
		IStatus status= new Status(IStatus.ERROR, ASTViewPlugin.getPluginId(), 0, message, e);
		ASTViewPlugin.log(status);
		ErrorDialog.openError(getSite().getShell(), "AST View", null, status); //$NON-NLS-1$
	}
	
	protected void performCreateBindings() {
		fCreateBindings= fCreateBindingsAction.isChecked();
		fDialogSettings.put(SETTINGS_NO_BINDINGS, !fCreateBindings);
		performRefresh();
	}
	
	protected void performStatementsRecovery() {
		fStatementsRecovery= fStatementsRecoveryAction.isChecked();
		fDialogSettings.put(SETTINGS_NO_STATEMENTS_RECOVERY, !fStatementsRecovery);
		performRefresh();
	}
	
	protected void performFilterNonRelevant() {
		boolean showNonRelevant= !fFilterNonRelevantAction.isChecked();
		fNonRelevantFilter.setShowNonRelevant(showNonRelevant);
		fDialogSettings.put(SETTINGS_SHOW_NON_RELEVANT, showNonRelevant);
		fViewer.refresh();
	}
	
	protected void performResolveBindingKey() {
		InputDialog dialog= new InputDialog(getSite().getShell(), "Resolve Binding Key", "Key: (optionally surrounded by <KEY: '> and <'>)", "", null);
		if (dialog.open() != Window.OK)
			return;

		String key= dialog.getValue();
		if (key.startsWith("KEY: '") && key.endsWith("'"))
			key= key.substring(6, key.length() - 1);
		ASTParser parser= ASTParser.newParser(fCurrentASTLevel);
		parser.setResolveBindings(true);
		if (fOpenable instanceof IJavaElement) {
			// TODO ASTView
//			parser.setProject(((IJavaElement) fOpenable).getJavaProject());
//			class MyASTRequestor extends ASTRequestor {
//				String fBindingKey;
//				IBinding fBinding;
//				public void acceptBinding(String bindingKey, IBinding binding) {
//					fBindingKey= bindingKey;
//					fBinding= binding;
//				}
//			}
//			MyASTRequestor requestor= new MyASTRequestor();
//			ASTAttribute item;
//			Object viewerInput= fViewer.getInput();
//			try {
//				parser.createASTs(new ICompilationUnit[0], new String[] { key }, requestor, null);
//				if (requestor.fBindingKey != null) {
//					item= new Binding(viewerInput, requestor.fBindingKey, requestor.fBinding, true);
//				} else {
//					item= new Error(viewerInput, "Key not resolved: " + key, null);
//				}
//			} catch (RuntimeException e) {
//				item= new Error(viewerInput, "Error resolving key: " + key, e);
//			}
//			fViewer.add(viewerInput, item);
//			fViewer.setSelection(new StructuredSelection(item), true);
		}
	}
	
	protected void performCreateBindingFromElement() {
		InputDialog dialog= new InputDialog(getSite().getShell(), "Create Binding from Java Element", "IJavaElement#getHandleIdentifier():", "", null);
		if (dialog.open() != Window.OK)
			return;
		
		String handleIdentifier= dialog.getValue();
		IJavaElement handle= JavaCore.create(handleIdentifier);
		
		Object viewerInput= fViewer.getInput();
		ASTAttribute item;
		if (handle == null) {
			item= new Error(viewerInput, "handleIdentifier not resolved: " + handleIdentifier, null);
		} else if (! handle.exists()) {
			item= new Error(viewerInput, "element does not exist: " + handleIdentifier, null);
		} else if (handle.getJavaProject() == null) {
			item= new Error(viewerInput, "getJavaProject() is null: " + handleIdentifier, null);
		} else {
			IJavaProject project= handle.getJavaProject();
			ASTParser parser= ASTParser.newParser(fCurrentASTLevel);
			parser.setProject(project);
			IBinding[] bindings= parser.createBindings(new IJavaElement[] { handle }, null);
			item= new Binding(viewerInput, handleIdentifier, bindings[0], true);
		}
		fViewer.add(viewerInput, item);
		fViewer.setSelection(new StructuredSelection(item), true);
	}
	
	protected void performDoubleClick() {
		if (fEditor == null) {
			return;
		}
		
		ISelection selection = fViewer.getSelection();
		Object obj = ((IStructuredSelection) selection).getFirstElement();

		boolean isTripleClick= (obj == fPreviousDouble);
		fPreviousDouble= isTripleClick ? null : obj;
		
		if (obj instanceof ExceptionAttribute) {
			RuntimeException exception= ((ExceptionAttribute) obj).getException();
			if (exception != null) {
				String label= ((ExceptionAttribute) obj).getLabel();
				showAndLogError("An error occurred while calculating an AST View Label:\n" + label, exception); //$NON-NLS-1$
				return;
			}
		}
		
		ASTNode node= null;
		if (obj instanceof ASTNode) {
			node= (ASTNode) obj;
			
		} else if (obj instanceof NodeProperty) {
			Object val= ((NodeProperty) obj).getNode();
			if (val instanceof ASTNode) {
				node= (ASTNode) val;
			}
			
		} else if (obj instanceof Binding) {
			IBinding binding= ((Binding) obj).getBinding();
			ASTNode declaring= fRoot.findDeclaringNode(binding);
			if (declaring != null) {
				fViewer.reveal(declaring);
				fViewer.setSelection(new StructuredSelection(declaring));
			} else {
				fViewer.getTree().getDisplay().beep();
			}
			return;
			
		} else if (obj instanceof ProblemNode) {
			ProblemNode problemNode= (ProblemNode) obj;
			EditorUtility.selectInEditor(fEditor, problemNode.getOffset(), problemNode.getLength());
			return;
			
		} else if (obj instanceof JavaElement) {
			IJavaElement javaElement= ((JavaElement) obj).getJavaElement();
			if (javaElement instanceof IPackageFragment) {
				// TODO ASTView
//				ShowInPackageViewAction showInPackageViewAction= new ShowInPackageViewAction(getViewSite());
//				showInPackageViewAction.run(javaElement);
			} else {
				try {
					IEditorPart editorPart= JavaUI.openInEditor(javaElement);
					if (editorPart != null)
						JavaUI.revealInEditor(editorPart, javaElement);
				} catch (PartInitException e) {
					showAndLogError("Could not open editor.", e); //$NON-NLS-1$
				} catch (JavaModelException e) {
					showAndLogError("Could not open editor.", e); //$NON-NLS-1$
				}
			}
			return;
			
		}
		
		if (node != null) {
			int offset= isTripleClick ? fRoot.getExtendedStartPosition(node) : node.getStartPosition();
			int length= isTripleClick ? fRoot.getExtendedLength(node) : node.getLength();
			EditorUtility.selectInEditor(fEditor, offset, length);
		}
	}
	
	protected void performAddToTray() {
		IStructuredSelection selection= (IStructuredSelection) fViewer.getSelection();
		Object firstElement= selection.getFirstElement();
		if (! fTrayRoots.contains(firstElement)) {
			fTrayRoots.add(firstElement);
			fTray.setInput(fTrayRoots);
		}
		if (fSash.getMaximizedControl() != null) {
			int trayHeight= fTray.getTree().getItemHeight() * (2 + TrayContentProvider.DEFAULT_CHILDREN_COUNT);
			int sashHeight= fSash.getClientArea().height;
			fSash.setWeights(new int[] { sashHeight - trayHeight, trayHeight });
			fSash.setMaximizedControl(null);
		}
		setTraySelection(selection);
	}
	
	private void setTraySelection(IStructuredSelection selection) {
		fTray.setSelection(selection, true);
		TreeItem[] itemSelection= fTray.getTree().getSelection();
		if (itemSelection.length > 0)
			fTray.getTree().setTopItem(itemSelection[0]);
	}

	protected void performTrayDoubleClick() {
		IStructuredSelection selection= (IStructuredSelection) fTray.getSelection();
		if (selection.size() != 1)
			return;
		Object obj = selection.getFirstElement();
		if (obj instanceof ExceptionAttribute) {
			RuntimeException exception= ((ExceptionAttribute) obj).getException();
			if (exception != null) {
				String label= ((ExceptionAttribute) obj).getLabel();
				showAndLogError("An error occurred while calculating an AST View Label:\n" + label, exception); //$NON-NLS-1$
				return;
			}
		}
		if (obj instanceof Binding) {
			Binding binding= (Binding) obj;
			fViewer.setSelection(new StructuredSelection(binding), true);
		}
	}
		
	protected void performDelete() {
		IStructuredSelection selection= (IStructuredSelection) fTray.getSelection();
		if (selection.size() != 1)
			return;
		Object obj = selection.getFirstElement();
		if (obj instanceof Binding) {
			int index= fTrayRoots.indexOf(obj);
			if (index != -1) {
				fTrayRoots.remove(index);
				fTray.setInput(fTrayRoots);
				int newSize= fTrayRoots.size();
				if (newSize == 0)
					return;
				else if (index == newSize)
					setTraySelection(new StructuredSelection(fTrayRoots.get(newSize - 1)));
				else
					setTraySelection(new StructuredSelection(fTrayRoots.get(index)));
			}
		}
	}
	
	public void setFocus() {
		fViewer.getControl().setFocus();
	}

	public ShowInContext getShowInContext() {
		return new ShowInContext(null, getSite().getSelectionProvider().getSelection());
	}
}