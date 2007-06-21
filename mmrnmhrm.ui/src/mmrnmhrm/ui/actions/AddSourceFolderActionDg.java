package mmrnmhrm.ui.actions;

import melnorme.lang.ui.ExceptionHandler;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.model.DeeModelManager;
import mmrnmhrm.core.model.DeeProject;
import mmrnmhrm.ui.DeeUI;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import util.Assert;

public class AddSourceFolderActionDg implements IObjectActionDelegate {
	
	private ISelection selection;
	IFolder folder;

	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}
	
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	public void run(IAction action) {
		if (selection instanceof IStructuredSelection) {
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
			} catch (CoreException e) {
				ExceptionHandler.handle(e, DeeUI.getActiveWorkbenchShell(),
						"AddFolderToBuildPath",
						"AddFolderToBuildPath :: message");
			}
		}		
	}


	
}