package descent.ui.actions;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;

import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;

import org.eclipse.jface.text.ITextSelection;

import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.progress.IProgressService;

import descent.core.IClassFile;
import descent.core.ICompilationUnit;
import descent.core.IJavaElement;
import descent.core.ILocalVariable;
import descent.core.IMember;
import descent.core.IPackageFragment;
import descent.core.IType;
import descent.core.JavaCore;
import descent.core.JavaModelException;
import descent.core.search.IJavaSearchScope;

import descent.internal.corext.util.JavaModelUtil;

import descent.ui.JavaElementLabelProvider;
import descent.ui.search.ElementQuerySpecification;
import descent.ui.search.QuerySpecification;

import descent.internal.ui.JavaPlugin;
import descent.internal.ui.actions.ActionUtil;
import descent.internal.ui.actions.OpenActionUtil;
import descent.internal.ui.actions.SelectionConverter;
import descent.internal.ui.javaeditor.JavaEditor;
import descent.internal.ui.search.JavaSearchQuery;
import descent.internal.ui.search.JavaSearchScopeFactory;
import descent.internal.ui.search.SearchMessages;
import descent.internal.ui.search.SearchUtil;
import descent.internal.ui.util.ExceptionHandler;

/**
 * Abstract class for Java search actions.
 * <p>
 * Note: This class is for internal use only. Clients should not use this class.
 * </p>
 * 
 * @since 2.0
 */
public abstract class FindAction extends SelectionDispatchAction {

	// A dummy which can't be selected in the UI
	private static final IJavaElement RETURN_WITHOUT_BEEP= JavaCore.create(JavaPlugin.getWorkspace().getRoot());
		
	private Class[] fValidTypes;
	private JavaEditor fEditor;	


	FindAction(IWorkbenchSite site) {
		super(site);
		fValidTypes= getValidTypes();
		init();
	}

	FindAction(JavaEditor editor) {
		this(editor.getEditorSite());
		fEditor= editor;
		setEnabled(SelectionConverter.canOperateOn(fEditor));
	}
	
	/**
	 * Called once by the constructors to initialize label, tooltip, image and help support of the action.
	 * To be overridden by implementors of this action.
	 */
	abstract void init();

	/**
	 * Called once by the constructors to get the list of the valid input types of the action.
	 * To be overridden by implementors of this action.
	 * @return the valid input types of the action
	 */
	abstract Class[] getValidTypes();
	
	private boolean canOperateOn(IStructuredSelection sel) {
		return sel != null && !sel.isEmpty() && canOperateOn(getJavaElement(sel, true));
	}
		
	boolean canOperateOn(IJavaElement element) {
		if (element == null || fValidTypes == null || fValidTypes.length == 0 || !ActionUtil.isOnBuildPath(element))
			return false;

		for (int i= 0; i < fValidTypes.length; i++) {
			if (fValidTypes[i].isInstance(element)) {
				if (element.getElementType() == IJavaElement.PACKAGE_FRAGMENT)
					return hasChildren((IPackageFragment)element);
				else
					return true;
			}
		}
		return false;
	}
	
	private boolean hasChildren(IPackageFragment packageFragment) {
		try {
			return packageFragment.hasChildren();
		} catch (JavaModelException ex) {
			return false;
		}
	}

	private IJavaElement getTypeIfPossible(IJavaElement o, boolean silent) {
		switch (o.getElementType()) {
			case IJavaElement.COMPILATION_UNIT:
				if (silent)
					return o;
				else
					return findType((ICompilationUnit)o, silent);
			case IJavaElement.CLASS_FILE:
				return findType((IClassFile)o);
			default:
				return o;				
		}
	}

	IJavaElement getJavaElement(IStructuredSelection selection, boolean silent) {
		if (selection.size() == 1) {
			Object firstElement= selection.getFirstElement();
			IJavaElement elem= null;
			if (firstElement instanceof IJavaElement) 
				elem= (IJavaElement) firstElement;
			else if (firstElement instanceof IAdaptable) 
				elem= (IJavaElement) ((IAdaptable) firstElement).getAdapter(IJavaElement.class);
			if (elem != null) {
				return getTypeIfPossible(elem, silent);
			}
			
		}
		return null;
	}

	private void showOperationUnavailableDialog() {
		MessageDialog.openInformation(getShell(), SearchMessages.JavaElementAction_operationUnavailable_title, getOperationUnavailableMessage()); 
	}	

	String getOperationUnavailableMessage() {
		return SearchMessages.JavaElementAction_operationUnavailable_generic; 
	}

	private IJavaElement findType(ICompilationUnit cu, boolean silent) {
		IType[] types= null;
		try {					
			types= cu.getAllTypes();
		} catch (JavaModelException ex) {
			if (JavaModelUtil.isExceptionToBeLogged(ex))
				ExceptionHandler.log(ex, SearchMessages.JavaElementAction_error_open_message); 
			if (silent)
				return RETURN_WITHOUT_BEEP;
			else
				return null;
		}
		if (types.length == 1 || (silent && types.length > 0))
			return types[0];
		if (silent)
			return RETURN_WITHOUT_BEEP;
		if (types.length == 0)
			return null;
		String title= SearchMessages.JavaElementAction_typeSelectionDialog_title; 
		String message = SearchMessages.JavaElementAction_typeSelectionDialog_message; 
		int flags= (JavaElementLabelProvider.SHOW_DEFAULT);						

		ElementListSelectionDialog dialog= new ElementListSelectionDialog(getShell(), new JavaElementLabelProvider(flags));
		dialog.setTitle(title);
		dialog.setMessage(message);
		dialog.setElements(types);
		
		if (dialog.open() == Window.OK)
			return (IType)dialog.getFirstResult();
		else
			return RETURN_WITHOUT_BEEP;
	}

	private IType findType(IClassFile cf) {
		/* TODO JDT UI binary
		IType mainType;
		try {					
			mainType= cf.getType();
		} catch (JavaModelException ex) {
			if (JavaModelUtil.isExceptionToBeLogged(ex))
				ExceptionHandler.log(ex, SearchMessages.JavaElementAction_error_open_message); 
			return null;
		}
		return mainType;
		*/
		return null;
	}
	
	/* 
	 * Method declared on SelectionChangedAction.
	 */
	public void run(IStructuredSelection selection) {
		IJavaElement element= getJavaElement(selection, false);
		if (element == null || !element.exists()) {
			showOperationUnavailableDialog();
			return;
		} 
		else if (element == RETURN_WITHOUT_BEEP)
			return;
		
		run(element);
	}

	/* 
	 * Method declared on SelectionChangedAction.
	 */
	public void run(ITextSelection selection) {
		if (!ActionUtil.isProcessable(getShell(), fEditor))
			return;
		try {
			String title= SearchMessages.SearchElementSelectionDialog_title; 
			String message= SearchMessages.SearchElementSelectionDialog_message; 
			
			IJavaElement[] elements= SelectionConverter.codeResolveForked(fEditor, true);
			if (elements.length > 0 && canOperateOn(elements[0])) {
				IJavaElement element= elements[0];
				if (elements.length > 1)
					element= OpenActionUtil.selectJavaElement(elements, getShell(), title, message);
				if (element != null)
					run(element);
			}
			else
				showOperationUnavailableDialog();
		} catch (InvocationTargetException ex) {
			String title= SearchMessages.Search_Error_search_title; 
			String message= SearchMessages.Search_Error_codeResolve; 
			ExceptionHandler.handle(ex, getShell(), title, message);
		} catch (InterruptedException e) {
			// ignore
		}
	}

	/* 
	 * Method declared on SelectionChangedAction.
	 */
	public void selectionChanged(IStructuredSelection selection) {
		setEnabled(canOperateOn(selection));
	}

	/* 
	 * Method declared on SelectionChangedAction.
	 */
	public void selectionChanged(ITextSelection selection) {
	}

	/**
	 * Executes this action for the given java element.
	 * @param element The java element to be found.
	 */
	public void run(IJavaElement element) {
		
		if (!ActionUtil.isProcessable(getShell(), element))
			return;
		
		// will return true except for debugging purposes.
		try {
			performNewSearch(element);
		} catch (JavaModelException ex) {
			ExceptionHandler.handle(ex, getShell(), SearchMessages.Search_Error_search_notsuccessful_title, SearchMessages.Search_Error_search_notsuccessful_message); 
		}
	}

	private void performNewSearch(IJavaElement element) throws JavaModelException {
		JavaSearchQuery query= new JavaSearchQuery(createQuery(element));
		if (query.canRunInBackground()) {
			/*
			 * This indirection with Object as parameter is needed to prevent the loading
			 * of the Search plug-in: the VM verifies the method call and hence loads the
			 * types used in the method signature, eventually triggering the loading of
			 * a plug-in (in this case ISearchQuery results in Search plug-in being loaded).
			 */
			SearchUtil.runQueryInBackground(query);
		} else {
			IProgressService progressService= PlatformUI.getWorkbench().getProgressService();
			/*
			 * This indirection with Object as parameter is needed to prevent the loading
			 * of the Search plug-in: the VM verifies the method call and hence loads the
			 * types used in the method signature, eventually triggering the loading of
			 * a plug-in (in this case it would be ISearchQuery).
			 */
			IStatus status= SearchUtil.runQueryInForeground(progressService, query);
			if (status.matches(IStatus.ERROR | IStatus.INFO | IStatus.WARNING)) {
				ErrorDialog.openError(getShell(), SearchMessages.Search_Error_search_title, SearchMessages.Search_Error_search_message, status); 
			}
		}
	}
	
	QuerySpecification createQuery(IJavaElement element) throws JavaModelException {
		JavaSearchScopeFactory factory= JavaSearchScopeFactory.getInstance();
		IJavaSearchScope scope= factory.createWorkspaceScope(true);
		String description= factory.getWorkspaceScopeDescription(true);
		return new ElementQuerySpecification(element, getLimitTo(), scope, description);
	}

	abstract int getLimitTo();

	IType getType(IJavaElement element) {
		if (element == null)
			return null;
		
		IType type= null;
		if (element.getElementType() == IJavaElement.TYPE)
			type= (IType)element;
		else if (element instanceof IMember)
			type= ((IMember)element).getDeclaringType();
		else if (element instanceof ILocalVariable) {
			type= (IType)element.getAncestor(IJavaElement.TYPE);
		}
		return type;
	}
	
	JavaEditor getEditor() {
		return fEditor;
	}
		
}
