package mmrnmhrm.ui.actions;

import mmrnmhrm.ui.DeeUI;
import mmrnmhrm.ui.ExceptionHandler;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class TestAction1 implements IWorkbenchWindowActionDelegate {

	public void init(IWorkbenchWindow window) {
	}

	public void dispose() {
	}

	public void run(IAction action) {
		IWorkspaceRunnable op = new IWorkspaceRunnable() {

			
			public void run(IProgressMonitor monitor) throws CoreException {
				//DeeCore.getWorkspace().copy(resources, outputPath, 0, null);
			}
		};

		
		try {
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			workspace.run(op, null);

		} catch (CoreException e) {
			ExceptionHandler.handle(e, DeeUI.getActiveWorkbenchShell(),
					"XptoTestAction",
					"XptoTestAction :: message");
		}

	}

	public void selectionChanged(IAction action, ISelection selection) {
		
	}



}
