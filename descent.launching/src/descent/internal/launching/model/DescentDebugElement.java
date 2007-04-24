package descent.internal.launching.model;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.DebugElement;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.core.model.IDebugTarget;

import descent.launching.DescentLaunching;
import descent.launching.IDescentLaunchConfigurationConstants;

public class DescentDebugElement extends DebugElement implements IDebugElement {
	
	protected IDebugTarget target;
	
	public DescentDebugElement(IDebugTarget target) {
		super(target);
		this.target = target;
	}

	public String getModelIdentifier() {
		return IDescentLaunchConfigurationConstants.ID_D_DEBUG_MODEL;
	}
	
	protected void abort(String message, Throwable e) throws DebugException {
		throw new DebugException(new Status(IStatus.ERROR, DescentLaunching.PLUGIN_ID, 
				DebugPlugin.INTERNAL_ERROR, message, e));
	}

}
