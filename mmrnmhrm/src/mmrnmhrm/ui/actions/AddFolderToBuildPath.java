package mmrnmhrm.ui.actions;

import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.model.DeeModel;
import mmrnmhrm.ui.DeeUI;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.internal.ui.util.ExceptionHandler;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import util.Assert;

public class AddFolderToBuildPath implements IObjectActionDelegate {
	
	private ISelection selection;
	IFolder folder;

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		// TODO Check it is Project Explorer part?
	}

	public void run(IAction action) {
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection sel = (IStructuredSelection) selection;
			Assert.isTrue(sel.size() == 1);
			
			final IFolder folder = (IFolder) sel.getFirstElement();
			IWorkspaceRunnable op = new IWorkspaceRunnable() {
				public void run(IProgressMonitor monitor) throws CoreException {
					DeeModel.getInstance().addSourceFolderToBuildPath(folder);
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

	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}
	
}
