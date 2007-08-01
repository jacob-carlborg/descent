package mmrnmhrm.ui.actions;

import melnorme.miscutil.Assert;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.model.DeeProject;
import mmrnmhrm.core.model.IDeeSourceRoot;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
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
		
		IStructuredSelection sel = (IStructuredSelection) selection;
		Assert.isTrue(sel.size() == 1);
		final IDeeSourceRoot entry = (IDeeSourceRoot) sel.getFirstElement();
		final DeeProject proj = (DeeProject) entry.getParent();
		
		final IWorkspaceRunnable op = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				proj.removeSourceRoot(entry);
				proj.saveProjectConfigFile();
			}
		};
		
		OperationsManager.executeOperation("Add Folder To Build Path", new ISimpleRunnable() {
			public void run() throws CoreException {
				DeeCore.run(op, proj.getProject(), null);
			}
		});

		return null;
	}

}
