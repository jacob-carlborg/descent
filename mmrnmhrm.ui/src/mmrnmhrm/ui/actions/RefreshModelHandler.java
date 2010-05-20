package mmrnmhrm.ui.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;


@Deprecated
public class RefreshModelHandler extends AbstractHandler {
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		/*try {
			IWorkspaceRunnable op = new IWorkspaceRunnable() {
				public void run(IProgressMonitor monitor) throws CoreException {
					//DeeModel.getRoot().updateElementRecursive();
					//DeeModel.getRoot().updateElementLazily();
				}
			};
			DeeCore.runSimpleOp(op);
		} catch (CoreException ce) {
			throw new ExecutionException("RefreshModelHandler error", ce);
		}*/
		return null;
	}
	
}
