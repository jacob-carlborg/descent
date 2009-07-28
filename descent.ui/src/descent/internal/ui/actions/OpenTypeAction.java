package descent.internal.ui.actions;

import org.eclipse.core.runtime.CoreException;

import org.eclipse.swt.widgets.Shell;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelection;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;

import descent.core.IMember;
import descent.core.IType;
import descent.core.search.IJavaSearchConstants;

import descent.internal.ui.IJavaHelpContextIds;
import descent.internal.ui.JavaPlugin;
import descent.internal.ui.JavaPluginImages;
import descent.internal.ui.JavaUIMessages;
import descent.internal.ui.dialogs.OpenTypeSelectionDialog2;
import descent.internal.ui.javaeditor.EditorUtility;
import descent.internal.ui.util.ExceptionHandler;

public class OpenTypeAction extends Action implements IWorkbenchWindowActionDelegate {
	
	public OpenTypeAction() {
		super();
		setText(JavaUIMessages.OpenTypeAction_label); 
		setDescription(JavaUIMessages.OpenTypeAction_description); 
		setToolTipText(JavaUIMessages.OpenTypeAction_tooltip); 
		setImageDescriptor(JavaPluginImages.DESC_TOOL_OPENTYPE);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IJavaHelpContextIds.OPEN_TYPE_ACTION);
	}

	public void run() {
		Shell parent= JavaPlugin.getActiveWorkbenchShell();
		OpenTypeSelectionDialog2 dialog= new OpenTypeSelectionDialog2(parent, false, 
			PlatformUI.getWorkbench().getProgressService(),
			null, IJavaSearchConstants.TYPE);
		dialog.setTitle(JavaUIMessages.OpenTypeAction_dialogTitle); 
		dialog.setMessage(JavaUIMessages.OpenTypeAction_dialogMessage); 
		
		int result= dialog.open();
		if (result != IDialogConstants.OK_ID)
			return;
		
		Object[] types= dialog.getResult();
		if (types != null && types.length > 0) {
			IMember type= (IMember)types[0];
			try {
				IEditorPart part= EditorUtility.openInEditor(type, true);
				EditorUtility.revealInEditor(part, type);
			} catch (CoreException x) {
				String title= JavaUIMessages.OpenTypeAction_errorTitle; 
				String message= JavaUIMessages.OpenTypeAction_errorMessage; 
				ExceptionHandler.handle(x, title, message);
			}
		}
	}

	//---- IWorkbenchWindowActionDelegate ------------------------------------------------

	public void run(IAction action) {
		run();
	}
	
	public void dispose() {
		// do nothing.
	}
	
	public void init(IWorkbenchWindow window) {
		// do nothing.
	}
	
	public void selectionChanged(IAction action, ISelection selection) {
		// do nothing. Action doesn't depend on selection.
	}
}