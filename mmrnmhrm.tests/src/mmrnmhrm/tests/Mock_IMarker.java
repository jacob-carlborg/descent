package mmrnmhrm.tests;

import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

public class Mock_IMarker implements IMarker {

	public void delete() throws CoreException {

	}

	public boolean exists() {
		return false;
	}

	public Object getAttribute(String attributeName) throws CoreException {
		return null;
	}

	public int getAttribute(String attributeName, int defaultValue) {
		return 0;
	}

	public String getAttribute(String attributeName, String defaultValue) {
		return null;
	}

	public boolean getAttribute(String attributeName, boolean defaultValue) {

		return false;
	}

	public Map getAttributes() throws CoreException {

		return null;
	}

	public Object[] getAttributes(String[] attributeNames) throws CoreException {

		return null;
	}

	public long getCreationTime() throws CoreException {

		return 0;
	}

	public long getId() {

		return 0;
	}

	public IResource getResource() {

		return null;
	}

	public String getType() throws CoreException {

		return null;
	}

	public boolean isSubtypeOf(String superType) throws CoreException {

		return false;
	}

	public void setAttribute(String attributeName, int value)
			throws CoreException {


	}

	public void setAttribute(String attributeName, Object value)
			throws CoreException {


	}

	public void setAttribute(String attributeName, boolean value)
			throws CoreException {


	}

	public void setAttributes(Map attributes) throws CoreException {


	}

	public void setAttributes(String[] attributeNames, Object[] values)
			throws CoreException {


	}

	public Object getAdapter(Class adapter) {

		return null;
	}

}
