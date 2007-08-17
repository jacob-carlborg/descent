package mmrnmhrm.ui.actions;

import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.model.DeeModel;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;


public class RefreshModelHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			IWorkspaceRunnable op = new IWorkspaceRunnable() {
				public void run(IProgressMonitor monitor) throws CoreException {
					//DeeModel.getRoot().updateElementRecursive();
					DeeModel.getRoot().updateElementLazily();
				}
			};
			DeeCore.runSimpleOp(op);
		} catch (CoreException ce) {
			throw new ExecutionException("RefreshModelHandler error", ce);
		}
		return null;
	}

}
