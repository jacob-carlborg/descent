package descent.internal.core;

import org.eclipse.core.runtime.IProgressMonitor;

import descent.core.IBuffer;
import descent.core.IOpenable;
import descent.core.JavaModelException;

/* TODO JDT PRIORITY REPLACE WITH REAL ONE */
public class Openable implements IOpenable {

	public void close() throws JavaModelException {
		
	}

	public String findRecommendedLineSeparator() throws JavaModelException {
		return null;
	}

	public IBuffer getBuffer() throws JavaModelException {
		return null;
	}

	public boolean hasUnsavedChanges() throws JavaModelException {
		return false;
	}

	public boolean isConsistent() throws JavaModelException {
		return false;
	}

	public boolean isOpen() {
		return false;
	}

	public void makeConsistent(IProgressMonitor progress) throws JavaModelException {
		
	}

	public void open(IProgressMonitor progress) throws JavaModelException {
		
	}

	public void save(IProgressMonitor progress, boolean force) throws JavaModelException {
		
	}

	public boolean canBufferBeRemovedFromCache(IBuffer buffer) {
		return false;
	}

	public String toStringWithAncestors() {
		return null;
	}

}
