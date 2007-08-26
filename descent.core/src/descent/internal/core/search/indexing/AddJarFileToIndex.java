package descent.internal.core.search.indexing;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;

import descent.core.JavaModelException;
import descent.core.search.SearchEngine;
import descent.core.search.SearchParticipant;
import descent.internal.compiler.util.SimpleLookupTable;
import descent.internal.compiler.util.Util;
import descent.internal.core.JavaModelManager;
import descent.internal.core.index.Index;
import descent.internal.core.search.JavaSearchDocument;
import descent.internal.core.search.processing.JobManager;

class AddJarFileToIndex extends IndexRequest {
	
	private final static String EXISTS = "OK"; //$NON-NLS-1$
	private final static String DELETED = "DELETED"; //$NON-NLS-1$
	
	IFile resource;

	public AddJarFileToIndex(IFile resource, IndexManager manager) {
		super(resource.getFullPath(), manager);
		this.resource = resource;
	}
	public AddJarFileToIndex(IPath jarPath, IndexManager manager) {
		// external JAR scenario - no resource
		super(jarPath, manager);
	}
	public boolean equals(Object o) {
		if (o instanceof AddJarFileToIndex) {
			if (this.resource != null)
				return this.resource.equals(((AddJarFileToIndex) o).resource);
			if (this.containerPath != null)
				return this.containerPath.equals(((AddJarFileToIndex) o).containerPath);
		}
		return false;
	}
	public int hashCode() {
		if (this.resource != null)
			return this.resource.hashCode();
		if (this.containerPath != null)
			return this.containerPath.hashCode();
		return -1;
	}
	public boolean execute(IProgressMonitor progressMonitor) {

		if (this.isCancelled || progressMonitor != null && progressMonitor.isCanceled()) return true;

		try {
			// if index is already cached, then do not perform any check
			// MUST reset the IndexManager if a jar file is changed
			Index index = this.manager.getIndexForUpdate(this.containerPath, false, /*do not reuse index file*/ false /*do not create if none*/);
			if (index != null) {
				if (JobManager.VERBOSE)
					descent.internal.core.util.Util.verbose("-> no indexing required (index already exists) for " + this.containerPath); //$NON-NLS-1$
				return true;
			}

			index = this.manager.getIndexForUpdate(this.containerPath, true, /*reuse index file*/ true /*create if none*/);
			if (index == null) {
				if (JobManager.VERBOSE)
					descent.internal.core.util.Util.verbose("-> index could not be created for " + this.containerPath); //$NON-NLS-1$
				return true;
			}
			ReadWriteMonitor monitor = index.monitor;
			if (monitor == null) {
				if (JobManager.VERBOSE)
					descent.internal.core.util.Util.verbose("-> index for " + this.containerPath + " just got deleted"); //$NON-NLS-1$//$NON-NLS-2$
				return true; // index got deleted since acquired
			}
			File root = null;
			try {
				monitor.enterWrite(); // ask permission to write
				if (resource != null) {
					URI location = this.resource.getLocationURI();
					if (location == null) return false;
					if (JavaModelManager.ZIP_ACCESS_VERBOSE)
						System.out.println("(" + Thread.currentThread() + ") [AddJarFileToIndex.execute()] Creating ZipFile on " + location.getPath()); //$NON-NLS-1$	//$NON-NLS-2$
					File file = null;
					try {
						file = descent.internal.core.util.Util.toLocalFile(location, progressMonitor);
					} catch (CoreException e) {
						if (JobManager.VERBOSE) {
							descent.internal.core.util.Util.verbose("-> failed to index " + location.getPath() + " because of the following exception:"); //$NON-NLS-1$ //$NON-NLS-2$
							e.printStackTrace();
						}
					}
					if (file == null) {
						if (JobManager.VERBOSE)
							descent.internal.core.util.Util.verbose("-> failed to index " + location.getPath() + " because the file could not be fetched"); //$NON-NLS-1$ //$NON-NLS-2$
						return false;
					}
					
					root = file;
					// absolute path relative to the workspace
				} else {
					if (JavaModelManager.ZIP_ACCESS_VERBOSE)
						System.out.println("(" + Thread.currentThread() + ") [AddJarFileToIndex.execute()] Creating ZipFile on " + this.containerPath); //$NON-NLS-1$	//$NON-NLS-2$
					// external file -> it is ok to use toFile()
					root = this.containerPath.toFile();
					// path is already canonical since coming from a library classpath entry
				}

				if (this.isCancelled) {
					if (JobManager.VERBOSE)
						descent.internal.core.util.Util.verbose("-> indexing of " + root.getName() + " has been cancelled"); //$NON-NLS-1$ //$NON-NLS-2$
					return false;
				}

				if (JobManager.VERBOSE)
					descent.internal.core.util.Util.verbose("-> indexing " + root.getName()); //$NON-NLS-1$
				long initialTime = System.currentTimeMillis();

				String[] paths = index.queryDocumentNames(""); // all file names //$NON-NLS-1$
				if (paths != null) {
					int max = paths.length;
					/* check integrity of the existing index file
					 * if the length is equal to 0, we want to index the whole jar again
					 * If not, then we want to check that there is no missing entry, if
					 * one entry is missing then we recreate the index
					 */
					SimpleLookupTable indexedFileNames = new SimpleLookupTable(max == 0 ? 33 : max + 11);
					for (int i = 0; i < max; i++) {
						indexedFileNames.put(paths[i], DELETED);
					}
					
					visitToAddToIndexedFileNames(root, root, indexedFileNames);	
					
					boolean needToReindex = indexedFileNames.elementSize != max; // a new file was added
					if (!needToReindex) {
						Object[] valueTable = indexedFileNames.valueTable;
						for (int i = 0, l = valueTable.length; i < l; i++) {
							if (valueTable[i] == DELETED) {
								needToReindex = true; // a file was deleted so re-index
								break;
							}
						}
						if (!needToReindex) {
							if (JobManager.VERBOSE)
								descent.internal.core.util.Util.verbose("-> no indexing required (index is consistent with library) for " //$NON-NLS-1$
								+ root.getName() + " (" //$NON-NLS-1$
								+ (System.currentTimeMillis() - initialTime) + "ms)"); //$NON-NLS-1$
							this.manager.saveIndex(index); // to ensure its placed into the saved state
							return true;
						}
					}
				}

				// Index the jar for the first time or reindex the jar in case the previous index file has been corrupted
				// index already existed: recreate it so that we forget about previous entries
				SearchParticipant participant = SearchEngine.getDefaultSearchParticipant();
				index = manager.recreateIndex(this.containerPath);
				if (index == null) {
					// failed to recreate index, see 73330
					manager.removeIndex(this.containerPath);
					return false;
				}
				
				visitToIndexDocuments(root, root, participant, index);
				this.manager.saveIndex(index);
				if (JobManager.VERBOSE)
					descent.internal.core.util.Util.verbose("-> done indexing of " //$NON-NLS-1$
						+ root.getName() + " (" //$NON-NLS-1$
						+ (System.currentTimeMillis() - initialTime) + "ms)"); //$NON-NLS-1$
			} finally {
				monitor.exitWrite(); // free write lock
			}
		} catch (IOException e) {
			if (JobManager.VERBOSE) {
				descent.internal.core.util.Util.verbose("-> failed to index " + this.containerPath + " because of the following exception:"); //$NON-NLS-1$ //$NON-NLS-2$
				e.printStackTrace();
			}
			manager.removeIndex(this.containerPath);
			return false;
		}
		return true;
	}
	
	
	private void visitToAddToIndexedFileNames(File superRoot, File root, SimpleLookupTable indexedFileNames) {
		if (root.isDirectory()) {
			File[] files = root.listFiles();
			for(File file : files) {
				if (file.isFile()) {
					if (Util.isJavaFileName(file.getName())) {
						indexedFileNames.put(relativePath(superRoot, file), EXISTS);
					}
				} else {
					visitToAddToIndexedFileNames(superRoot, file, indexedFileNames);
				}
			}
		}
	}
	
	private void visitToIndexDocuments(File superRoot, File root, SearchParticipant participant, Index index) {
		if (root.isDirectory()) {
			File[] files = root.listFiles();
			for(File file : files) {
				if (file.isFile()) {
					if (Util.isJavaFileName(file.getName())) {
						try {
							char[] charContents = descent.internal.core.util.Util.getFileContentsAsCharArray(file);
							JavaSearchDocument entryDocument = new JavaSearchDocument(file.getAbsolutePath(), participant, charContents);
							this.manager.indexDocument(entryDocument, participant, index, this.containerPath);
						} catch (JavaModelException e) {
							e.printStackTrace();
						}
					}
				} else {
					visitToIndexDocuments(superRoot, file, participant, index);
				}
			}
		}
	}
	
	private String relativePath(File outer, File inner) {
		return outer.toString().substring(inner.toString().length());
	}
	
	protected Integer updatedIndexState() {
		return IndexManager.REBUILDING_STATE;
	}
	public String toString() {
		return "indexing " + this.containerPath.toString(); //$NON-NLS-1$
	}
}
