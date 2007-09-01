package mmrnmhrm.ui.actions;

import melnorme.miscutil.ArrayUtil;
import melnorme.miscutil.Assert;
import mmrnmhrm.core.DeeCore;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.IScriptProject;
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
		
		final IStructuredSelection sel = (IStructuredSelection) selection;
		Assert.isTrue(sel.size() >= 1);
		
		final IResource res = (IResource) sel.getFirstElement();
		
		final IWorkspaceRunnable op = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {

				IScriptProject project = DLTKCore.create(res.getProject());
				IBuildpathEntry[] entries = project.getRawBuildpath();
				IBuildpathEntry entry = DLTKCore.newLibraryEntry(res.getFullPath());
				// TODO: validate new entry?
				IBuildpathEntry[] newEntries = ArrayUtil.concat(entries, entry);
				project.setRawBuildpath(newEntries, monitor);
			}
		};
		
		OperationsManager.executeOperation("Add Folder Library To Build Path", new ISimpleRunnable() {
			public void run() throws CoreException {
				DeeCore.run(op, null);
			}
		});

		return null;
	}

}
