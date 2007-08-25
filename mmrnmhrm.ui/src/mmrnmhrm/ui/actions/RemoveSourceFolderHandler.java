package mmrnmhrm.ui.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;


/**
 */
public class RemoveSourceFolderHandler extends AbstractHandler {


	public Object execute(ExecutionEvent event) throws ExecutionException {

		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if(!(selection instanceof IStructuredSelection))
			return null;
		/*
		IStructuredSelection sel = (IStructuredSelection) selection;
		Assert.isTrue(sel.size() == 1);
		final IProjectFragment entry = (IProjectFragment) sel.getFirstElement();
		
		final IWorkspaceRunnable op = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				DeeProject.removeSrcFolderEntry(entry);
			}
		};
		
		OperationsManager.executeOperation("Add Folder To Build Path", new ISimpleRunnable() {
			public void run() throws CoreException {
				DeeCore.run(op, null, null);
			}
		});
*/
		return null;
	}

}
