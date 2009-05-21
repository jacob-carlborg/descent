package descent.internal.core.ctfe;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.DebugElement;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.core.model.IDebugTarget;

import descent.core.JavaCore;
import descent.core.ctfe.IDescentCtfeLaunchConfigurationConstants;

public class DescentCtfeDebugElement extends DebugElement implements IDebugElement {
	
	protected IDebugTarget target;
	
	public DescentCtfeDebugElement(IDebugTarget target) {
		super(target);
		this.target = target;
	}

	public final String getModelIdentifier() {
		return IDescentCtfeLaunchConfigurationConstants.ID_D_DEBUG_MODEL;
	}
	
	protected void abort(String message, Throwable e) throws DebugException {
		throw new DebugException(new Status(IStatus.ERROR, JavaCore.PLUGIN_ID, 
				DebugPlugin.INTERNAL_ERROR, message, e));
	}

}
