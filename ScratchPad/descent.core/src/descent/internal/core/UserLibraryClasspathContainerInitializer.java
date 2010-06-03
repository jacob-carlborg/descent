package descent.internal.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

import descent.core.ClasspathContainerInitializer;
import descent.core.IClasspathContainer;
import descent.core.IJavaProject;
import descent.core.JavaCore;

/**
 *
 */
public class UserLibraryClasspathContainerInitializer extends ClasspathContainerInitializer {

	/* (non-Javadoc)
	 * @see descent.core.ClasspathContainerInitializer#initialize(org.eclipse.core.runtime.IPath, descent.core.IJavaProject)
	 */
	public void initialize(IPath containerPath, IJavaProject project) throws CoreException {
		if (isUserLibraryContainer(containerPath)) {
			String userLibName= containerPath.segment(1);
						
			UserLibrary entries= UserLibraryManager.getUserLibrary(userLibName);
			if (entries != null) {
				UserLibraryClasspathContainer container= new UserLibraryClasspathContainer(userLibName);
				JavaCore.setClasspathContainer(containerPath, new IJavaProject[] { project }, 	new IClasspathContainer[] { container }, null);
			}
		}
	}
	
	private boolean isUserLibraryContainer(IPath path) {
		return path != null && path.segmentCount() == 2 && JavaCore.USER_LIBRARY_CONTAINER_ID.equals(path.segment(0));
	}
	
	/* (non-Javadoc)
	 * @see descent.core.ClasspathContainerInitializer#canUpdateClasspathContainer(org.eclipse.core.runtime.IPath, descent.core.IJavaProject)
	 */
	public boolean canUpdateClasspathContainer(IPath containerPath, IJavaProject project) {
		return isUserLibraryContainer(containerPath);
	}

	/**
	 * @see descent.core.ClasspathContainerInitializer#requestClasspathContainerUpdate(org.eclipse.core.runtime.IPath, descent.core.IJavaProject, descent.core.IClasspathContainer)
	 */
	public void requestClasspathContainerUpdate(IPath containerPath, IJavaProject project, IClasspathContainer containerSuggestion) throws CoreException {
		if (isUserLibraryContainer(containerPath)) {
			String name= containerPath.segment(1);
			if (containerSuggestion != null) {
				UserLibrary library= new UserLibrary(containerSuggestion.getClasspathEntries(), containerSuggestion.getKind() == IClasspathContainer.K_SYSTEM);
				UserLibraryManager.setUserLibrary(name, library, null); // should use a real progress monitor
			} else {
				UserLibraryManager.setUserLibrary(name, null, null); // should use a real progress monitor
			}
		}
	}

	/**
	 * @see descent.core.ClasspathContainerInitializer#getDescription(org.eclipse.core.runtime.IPath, descent.core.IJavaProject)
	 */
	public String getDescription(IPath containerPath, IJavaProject project) {
		if (isUserLibraryContainer(containerPath)) {
			return containerPath.segment(1);
		}
		return super.getDescription(containerPath, project);
	}

	/* (non-Javadoc)
	 * @see descent.core.ClasspathContainerInitializer#getComparisonID(org.eclipse.core.runtime.IPath, descent.core.IJavaProject)
	 */
	public Object getComparisonID(IPath containerPath, IJavaProject project) {
		return containerPath;
	}
}
