package descent.internal.core;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import descent.core.IClasspathContainer;
import descent.core.IClasspathEntry;
import descent.core.JavaCore;

/**
 *
 */
public class UserLibraryClasspathContainer implements IClasspathContainer {
	
	private String name;
	
	public UserLibraryClasspathContainer(String libName) {
		this.name= libName;
	}
	
	private UserLibrary getUserLibrary() {
		return UserLibraryManager.getUserLibrary(this.name);
	}

	/* (non-Javadoc)
	 * @see descent.core.IClasspathContainer#getClasspathEntries()
	 */
	public IClasspathEntry[] getClasspathEntries() {
		UserLibrary library= getUserLibrary();
		if (library != null) {
			return library.getEntries();
		}
		return new IClasspathEntry[0];
		
	}

	/* (non-Javadoc)
	 * @see descent.core.IClasspathContainer#getDescription()
	 */
	public String getDescription() {
		return this.name;
	}

	/* (non-Javadoc)
	 * @see descent.core.IClasspathContainer#getKind()
	 */
	public int getKind() {
		UserLibrary library= getUserLibrary();
		if (library != null && library.isSystemLibrary()) {
			return K_SYSTEM;
		}
		return K_APPLICATION;
	}

	/* (non-Javadoc)
	 * @see descent.core.IClasspathContainer#getPath()
	 */
	public IPath getPath() {
		return new Path(JavaCore.USER_LIBRARY_CONTAINER_ID).append(this.name);
	}
}
