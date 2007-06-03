package mmrnmhrm.ui.actions;

import melnorme.util.ui.actions.WorkbenchWindowActionDelegate;
import mmrnmhrm.ui.ExceptionHandler;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;

public class TestAction1 extends WorkbenchWindowActionDelegate {

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
			ExceptionHandler.handle(e, window.getShell(),
					"XptoTestAction",
					"XptoTestAction :: message");
		}

	}

}
