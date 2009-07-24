package descent.ui.actions;

import org.eclipse.core.runtime.CoreException;

import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;

import org.eclipse.jface.text.ITextSelection;

import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.PlatformUI;

import descent.core.Flags;
import descent.core.IJavaElement;
import descent.core.IMethod;
import descent.core.IType;
import descent.core.JavaModelException;

import descent.internal.corext.util.Messages;
import descent.internal.corext.util.MethodOverrideTester;
import descent.internal.corext.util.SuperTypeHierarchyCache;

import descent.internal.ui.IJavaHelpContextIds;
import descent.internal.ui.JavaPlugin;
import descent.internal.ui.actions.ActionMessages;
import descent.internal.ui.actions.ActionUtil;
import descent.internal.ui.actions.OpenActionUtil;
import descent.internal.ui.actions.SelectionConverter;
import descent.internal.ui.javaeditor.JavaEditor;

/**
 * The action opens a Java editor on the selected method's super implementation.
 * <p>
 * The action is applicable to selections containing elements of type <code>
 * IMethod</code>.
 * 
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 * 
 * @since 2.0
 */
public class OpenSuperImplementationAction extends SelectionDispatchAction {

	private JavaEditor fEditor;

	/**
	 * Creates a new <code>OpenSuperImplementationAction</code>. The action requires
	 * that the selection provided by the site's selection provider is of type <code>
	 * org.eclipse.jface.viewers.IStructuredSelection</code>.
	 * 
	 * @param site the site providing context information for this action
	 */
	public OpenSuperImplementationAction(IWorkbenchSite site) {
		super(site);
		setText(ActionMessages.OpenSuperImplementationAction_label); 
		setDescription(ActionMessages.OpenSuperImplementationAction_description); 
		setToolTipText(ActionMessages.OpenSuperImplementationAction_tooltip); 
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IJavaHelpContextIds.OPEN_SUPER_IMPLEMENTATION_ACTION);
	}
	
	/**
	 * Creates a new <code>OpenSuperImplementationAction</code>. The action requires
	 * that the selection provided by the given selection provider is of type <code>
	 * org.eclipse.jface.viewers.IStructuredSelection</code>.
	 * 
	 * @param site the site providing context information for this action
	 * @param provider a special selection provider which is used instead 
	 *  of the site's selection provider or <code>null</code> to use the site's
	 *  selection provider
	 * 
	 * @since 3.2
	 * @deprecated Use {@link #setSpecialSelectionProvider(ISelectionProvider)} instead. This API will be
	 * removed after 3.2 M5.
     */
    public OpenSuperImplementationAction(IWorkbenchSite site, ISelectionProvider provider) {
        this(site);
        setSpecialSelectionProvider(provider);
    }
    

	
	/**
	 * Note: This constructor is for internal use only. Clients should not call this constructor.
	 * @param editor the Java editor
	 */
	public OpenSuperImplementationAction(JavaEditor editor) {
		this(editor.getEditorSite());
		fEditor= editor;
		setEnabled(SelectionConverter.canOperateOn(fEditor));
	}
	
	/* (non-Javadoc)
	 * Method declared on SelectionDispatchAction.
	 */
	public void selectionChanged(ITextSelection selection) {
	}

	/* (non-Javadoc)
	 * Method declared on SelectionDispatchAction.
	 */
	public void selectionChanged(IStructuredSelection selection) {
		IMethod method= getMethod(selection);
		
		setEnabled(method != null && checkMethod(method));
	}
	
	/* (non-Javadoc)
	 * Method declared on SelectionDispatchAction.
	 */
	public void run(ITextSelection selection) {
		if (!ActionUtil.isProcessable(getShell(), fEditor))
			return;
		IJavaElement element= elementAtOffset();
		if (element == null || !(element instanceof IMethod)) {
			MessageDialog.openInformation(getShell(), getDialogTitle(), ActionMessages.OpenSuperImplementationAction_not_applicable); 
			return;
		}
		run((IMethod) element);
	}
	
	/* (non-Javadoc)
	 * Method declared on SelectionDispatchAction.
	 */
	public void run(IStructuredSelection selection) {
		run(getMethod(selection));
	}
	
	/*
	 * No Javadoc since the method isn't meant to be public but is
	 * since the beginning
	 */
	public void run(IMethod method) {
		if (method == null)
			return;		
		if (!ActionUtil.isProcessable(getShell(), method))
			return;
		
		if (!checkMethod(method)) {
			MessageDialog.openInformation(getShell(), getDialogTitle(), 
				Messages.format(ActionMessages.OpenSuperImplementationAction_no_super_implementation, method.getElementName())); 
			return;
		}		

		try {
			IMethod impl= findSuperImplementation(method);
			if (impl != null) {
				OpenActionUtil.open(impl);
			}
		} catch (CoreException e) {
			JavaPlugin.log(e);
			String message= ActionMessages.OpenSuperImplementationAction_error_message; 
			ErrorDialog.openError(getShell(), getDialogTitle(), message, e.getStatus());
		}
	}
	
	private IMethod findSuperImplementation(IMethod method) throws JavaModelException {
		MethodOverrideTester tester= SuperTypeHierarchyCache.getMethodOverrideTester(method.getDeclaringType());
		return tester.findOverriddenMethod(method, false);
	}
	
	
	private IMethod getMethod(IStructuredSelection selection) {
		if (selection.size() != 1)
			return null;
		Object element= selection.getFirstElement();
		if (element instanceof IMethod) {
			return (IMethod) element;
		}
		return null;
	}
	
	private boolean checkMethod(IMethod method) {
		try {
			long flags= method.getFlags();
			if (!Flags.isStatic(flags) && !Flags.isPrivate(flags)) {
				IType declaringType= method.getDeclaringType();
				if (SuperTypeHierarchyCache.hasInCache(declaringType)) {
					if (findSuperImplementation(method) == null) {
						return false;
					}
				}
				return true;
			}
		} catch (JavaModelException e) {
			if (!e.isDoesNotExist()) {
				JavaPlugin.log(e);
			}
		}
		return false;
	}
	
	private IJavaElement elementAtOffset() {
		try {
			return SelectionConverter.getElementAtOffset(fEditor);
		} catch(JavaModelException e) {
		}
		return null;
	}
	
	private static String getDialogTitle() {
		return ActionMessages.OpenSuperImplementationAction_error_title; 
	}		
}
