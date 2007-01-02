package descent.internal.core.search.indexing;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;

import descent.internal.core.JavaProject;

// TODO JDT implement with real one
public class IndexManager {

	public void removeIndex(IPath entryPath) {		
	}

	public void indexLibrary(IPath entryPath, IProject project) {		
	}

	public void discardJobs(String name) {
	}

	public void reset() {
	}

	public void removeIndexFamily(IPath fullPath) {		
	}

	public void indexAll(IProject project) {
	}

	public void addBinary(IFile file, IPath binaryFolderPath) {
	}

	public void remove(String containerRelativePath, IPath binaryFolderPath) {
	}

	public void cleanUpIndexes() {
	}

	public int awaitingJobsCount() {
		return 0;
	}

	public void shutdown() {
	}

	public void removeSourceFolderFromIndex(JavaProject project, IPath path, char[][] inclusionPatterns, char[][] exclusionPatterns) {
	}

	public void indexSourceFolder(JavaProject project, IPath path, char[][] inclusionPatterns, char[][] exclusionPatterns) {
	}

}
