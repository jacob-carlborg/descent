package mmrnmhrm.ui.actions;

import melnorme.lang.ui.ExceptionHandler;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.model.DeeModelManager;
import mmrnmhrm.core.model.DeeProject;
import mmrnmhrm.ui.DeeUI;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import util.Assert;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class AddSourceFolderHandler extends AbstractHandler {
	/**
	 * The constructor.
	 */
	public AddSourceFolderHandler() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {

		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if(!(selection instanceof IStructuredSelection))
			return null;
		
		IStructuredSelection sel = (IStructuredSelection) selection;
		Assert.isTrue(sel.size() == 1);
		
		final IFolder folder = (IFolder) sel.getFirstElement();
		IWorkspaceRunnable op = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				// TODO, envelop in a model operation
				IProject project = folder.getProject();
				DeeProject proj = DeeModelManager.getLangProject(project);
				proj.createAddSourceFolder(folder);
				proj.saveProjectConfigFile();
			}
		};
		
		try {
			DeeCore.run(op, folder.getProject(), null);
		} catch (CoreException ce) {
			ExceptionHandler.handle(ce, DeeUI.getActiveWorkbenchShell(),
					"AddFolderToBuildPath", "AddFolderToBuildPath Error: " + ce);

		}

		return null;
	}

}
