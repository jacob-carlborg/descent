package descent.internal.core.ctfe;

import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamsProxy;

public class CtfeProcess extends PlatformObject implements IProcess {
	
	private final ILaunch launch;
	private boolean terminated;

	public CtfeProcess(ILaunch launch) {
		this.launch = launch;
		
		launch.addProcess(this);
		fireCreationEvent();
	}

	public String getAttribute(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	public int getExitValue() throws DebugException {
		return 0;
	}

	public String getLabel() {
		return "Descent Compile-Time process";
	}

	public ILaunch getLaunch() {
		return launch;
	}

	public IStreamsProxy getStreamsProxy() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setAttribute(String key, String value) {
	}

	public Object getAdapter(Class adapter) {
		if (adapter.equals(IProcess.class)) {
			return this;
		}
		if (adapter.equals(IDebugTarget.class)) {
			ILaunch launch = getLaunch();
			IDebugTarget[] targets = launch.getDebugTargets();
			for (int i = 0; i < targets.length; i++) {
				if (this.equals(targets[i].getProcess())) {
					return targets[i];
				}
			}
			return null;
		}
		if (adapter.equals(ILaunch.class)) {
			return getLaunch();
		}
		//CONTEXTLAUNCHING
		if(adapter.equals(ILaunchConfiguration.class)) {
			return getLaunch().getLaunchConfiguration();
		}
		return super.getAdapter(adapter);
	}

	public boolean canTerminate() {
		return !isTerminated();
	}

	public boolean isTerminated() {
		return terminated;
	}

	public void terminate() throws DebugException {
		this.terminated = true;
		fireTerminateEvent();
	}
	
	/**
	 * Fires a creation event.
	 */
	protected void fireCreationEvent() {
		fireEvent(new DebugEvent(this, DebugEvent.CREATE));
	}
	
	/**
	 * Fires a terminate event.
	 */
	protected void fireTerminateEvent() {
		fireEvent(new DebugEvent(this, DebugEvent.TERMINATE));
	}
	
	/**
	 * Fires the given debug event.
	 * 
	 * @param event debug event to fire
	 */
	protected void fireEvent(DebugEvent event) {
		DebugPlugin manager= DebugPlugin.getDefault();
		if (manager != null) {
			manager.fireDebugEventSet(new DebugEvent[]{event});
		}
	}

}
