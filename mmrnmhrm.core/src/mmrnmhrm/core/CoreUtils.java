package mmrnmhrm.core;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.SubProgressMonitor;

public class CoreUtils {

	public static void overwriteCopy(IContainer source, IFolder dest,
			SubProgressMonitor monitor) throws CoreException {
		IResource[] members = source.members();
		for (int i = 0; i < members.length; i++) {
			IResource resource = members[i];
			IPath destPath = dest.getFullPath().append(resource.getName());
			IResource target = LangCore.getWorkspaceRoot().findMember(destPath);
			if(resource.getType() == IResource.FILE) {
				if(target == null)
					resource.copy(destPath, IResource.REPLACE | IResource.FORCE, monitor);
			} else if(resource.getType() == IResource.FOLDER) {
				IContainer container = (IContainer) resource;
				IFolder dstFolder = dest.getFolder(container.getName());
				if(target == null)
					dstFolder.create(true, true, monitor);
				if(dstFolder.exists()) // Make sure the target is really a folder
					overwriteCopy(container, dstFolder, monitor);
			}
			
		}
	}

}
