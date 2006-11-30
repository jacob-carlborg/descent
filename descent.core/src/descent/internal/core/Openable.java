package descent.internal.core;

import org.eclipse.core.runtime.IProgressMonitor;

import descent.core.IBuffer;
import descent.core.IOpenable;
import descent.core.JavaModelException;

/* TODO JDT PRIORITY REPLACE WITH REAL ONE */
public class Openable implements IOpenable {

	public void close() throws JavaModelException {
		// TODO Auto-generated method stub
		
	}

	public String findRecommendedLineSeparator() throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	public IBuffer getBuffer() throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasUnsavedChanges() throws JavaModelException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isConsistent() throws JavaModelException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isOpen() {
		// TODO Auto-generated method stub
		return false;
	}

	public void makeConsistent(IProgressMonitor progress) throws JavaModelException {
		// TODO Auto-generated method stub
		
	}

	public void open(IProgressMonitor progress) throws JavaModelException {
		// TODO Auto-generated method stub
		
	}

	public void save(IProgressMonitor progress, boolean force) throws JavaModelException {
		// TODO Auto-generated method stub
		
	}

	public boolean canBufferBeRemovedFromCache(IBuffer buffer) {
		// TODO Auto-generated method stub
		return false;
	}

	public String toStringWithAncestors() {
		// TODO Auto-generated method stub
		return null;
	}

}
