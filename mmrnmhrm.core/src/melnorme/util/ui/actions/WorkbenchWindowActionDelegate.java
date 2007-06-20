package melnorme.util.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public abstract class WorkbenchWindowActionDelegate implements IWorkbenchWindowActionDelegate {

	protected IWorkbenchWindow window;
	
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

	public void dispose() {
	}
	
	public void selectionChanged(IAction action, ISelection selection) {
	}

}