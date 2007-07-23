package mmrnmhrm.ui.actions;

import melnorme.lang.ui.ExceptionHandler;
import mmrnmhrm.ui.DeePlugin;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

public class OperationsManager {

	public static OperationsManager instance = new OperationsManager();
	
	public static OperationsManager getInstance() {
		return instance;
	}	
	
	public boolean unitTestMode = false;
	
	public String opName;
	public IStatus opStatus;
	public int opResult;
	public String opMessage;
	
	
	public void aboutToDoOperation() {
		opMessage = null;
		opStatus = null;
		opResult = IStatus.OK;
	}
	
	
	public static void doOperation2(String opName, IWorkspaceRunnable op) {
		getInstance().doOperation(opName, DeePlugin.getActiveWorkbenchShell(), op);
	}

	public void doOperation(String opName, IWorkspaceRunnable op) {
		doOperation(opName, DeePlugin.getActiveWorkbenchShell(), op);
	}
	
	public boolean doOperation(String opName, Shell shell, IWorkspaceRunnable op) {
		this.opName = opName;
		aboutToDoOperation();
		
		try {
			op.run(null);
		} catch (CoreException ce) {
			ExceptionHandler.handle(ce, opName, "Execution Error");
			opResult = IStatus.ERROR; 
		} catch(RuntimeException re) {
			opResult = IStatus.ERROR;
			throw re;
		}
		
		return true;
	}

	
	private void setError(String msg) {
		opResult = IStatus.ERROR;
		opMessage = msg;
	}

	public void setWarning(String msg) {
		opResult = IStatus.WARNING;
		opMessage = msg;
	}
	
	public void setInfo(String msg) {
		opResult = IStatus.INFO;
		opMessage = msg;
	}

	public static void openWarning(Shell shell, String title, String message) {
		getInstance().setWarning(message);
		if(getInstance().unitTestMode)
			return;
		
		MessageDialog.openWarning(shell, title, message);
	}
	
	public static void openInfo(Shell shell, String title, String message) {
		getInstance().setInfo(message);
		if(getInstance().unitTestMode)
			return;
		
		MessageDialog.openInformation(shell, title, message);
	}


	public static void openError(Shell shell, String title, String message) {
		getInstance().setError(message);
		if(getInstance().unitTestMode)
			return;
		
		MessageDialog.openError(shell, title, message);	}



	
}
