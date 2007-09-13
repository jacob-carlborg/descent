package mmrnmhrm.core;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubProgressMonitor;

import static melnorme.miscutil.Assert.assertTrue;

public class CoreUtils {

	public static void copyContentsOverwriting(IContainer source, IContainer dest,
			SubProgressMonitor monitor) throws CoreException {
		assertTrue(dest.exists());
		
		IResource[] members = source.members();
		for (int i = 0; i < members.length; i++) {
			IResource srcResource = members[i];
			IPath destPath = dest.getFullPath().append(srcResource.getName());
			IResource dstResource = LangCore.getWorkspaceRoot().findMember(destPath);
		
			if(srcResource.getType() == IResource.FILE) {
				if(dstResource == null) // if dst does not exist
					srcResource.copy(destPath, IResource.FORCE, monitor);
			} else if(srcResource.getType() == IResource.FOLDER) {
				IFolder srcFolder = (IFolder) srcResource;
				if(srcFolder.equals(dest)) {
					continue; // We should not copy a folder into itself
				}

				IFolder dstFolder = dest.getFolder(new Path(srcFolder.getName()));
				
				if(dstResource == null) {
					dstFolder.create(true, true, monitor);
				} else if(dstResource.getType() == IResource.FILE) {
					dstResource.delete(true, monitor);
					dstFolder.create(true, true, monitor);
				}
				assertTrue(dstFolder.exists());
				copyContentsOverwriting(srcFolder, dstFolder, monitor);
			}
			
		}
	}

}
