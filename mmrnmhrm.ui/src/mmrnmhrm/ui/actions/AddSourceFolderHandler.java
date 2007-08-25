package mmrnmhrm.ui.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;


/**
 */
public class AddSourceFolderHandler extends AbstractHandler {


	public Object execute(ExecutionEvent event) throws ExecutionException {

		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if(!(selection instanceof IStructuredSelection))
			return null;
		/*
		final IStructuredSelection sel = (IStructuredSelection) selection;
		Assert.isTrue(sel.size() >= 1);

		
		//final IFolder folder = (IFolder) sel.getFirstElement();
		final IWorkspaceRunnable op = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				for (Iterator iterator = sel.iterator(); iterator.hasNext();) {
					IFolder folder = (IFolder) iterator.next();
					IProject project = folder.getProject();
					DeeProject proj = DeeModel.getLangProject(project);
					if(proj.getSourceRoot(folder) == null) {
						proj.createAddSourceFolder(folder);
						proj.saveProjectConfigFile();
					}
				}
			}
		};
		
		OperationsManager.executeOperation("Add Folder To Build Path", new ISimpleRunnable() {
			public void run() throws CoreException {
				DeeCore.run(op, null);
			}
		});
*/
		return null;
	}

}
