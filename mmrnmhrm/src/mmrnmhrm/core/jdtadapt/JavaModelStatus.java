package mmrnmhrm.core.jdtadapt;


import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Status;

/**
 * JAVA STUB: @see org.eclipse.jdt.internal.core.JavaModelStatus
 */

public class JavaModelStatus extends Status implements IJavaModelStatus, IJavaModelStatusConstants, IResourceStatus {

	public JavaModelStatus(int severity, String pluginId, int code, String message, Throwable exception) {
		super(severity, pluginId, code, message, exception);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Constructs an Java model status with no corresponding elements.
	 */
	public JavaModelStatus(int code, Throwable throwable) {
		super(ERROR, JavaCore.PLUGIN_ID, code, "JavaModelStatus", throwable); //$NON-NLS-1$
	}

	public IPath getPath() {
		// TODO Auto-generated method stub
		return null;
	}

}
