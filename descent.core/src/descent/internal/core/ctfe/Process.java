package descent.internal.core.ctfe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.IStreamListener;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamMonitor;
import org.eclipse.debug.core.model.IStreamsProxy;

import descent.core.ctfe.IOutput;

public class Process extends PlatformObject implements IProcess, IStreamsProxy, IOutput {
	
	private static class StreamMonitor implements IStreamMonitor {
		
		private List<IStreamListener> fListeners = new ArrayList<IStreamListener>();
		private StringBuilder fContents = new StringBuilder();
		
		public final void addListener(IStreamListener listener) {
			this.fListeners.add(listener);
		}
		
		public final void removeListener(IStreamListener listener) {
			this.fListeners.remove(listener);
		}
		
		public String getContents() {
			return fContents.toString();
		}
		
		public final void append(String contents) {
			fContents.append(contents + "\n");
			
			for(IStreamListener listener : fListeners) {
				listener.streamAppended(contents + "\n", this);
			}
		}
		
	}
	
	private final ILaunch fLaunch;
	private boolean fTerminated;
	private StreamMonitor fErrorStreamMonitor;
	private StreamMonitor fOutputStreamMonitor;

	public Process(ILaunch launch) {
		this.fLaunch = launch;
		this.fErrorStreamMonitor = new StreamMonitor();
		this.fOutputStreamMonitor = new StreamMonitor();
		
		launch.addProcess(this);
		fireCreationEvent();
	}

	public String getAttribute(String key) {
		return null;
	}

	public int getExitValue() throws DebugException {
		return 0;
	}

	public String getLabel() {
		return "Descent Compile-Time process";
	}

	public ILaunch getLaunch() {
		return fLaunch;
	}

	public IStreamsProxy getStreamsProxy() {
		return this;
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
		return fTerminated;
	}

	public void terminate() throws DebugException {
		this.fTerminated = true;
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

	public IStreamMonitor getErrorStreamMonitor() {
		return fErrorStreamMonitor;
	}

	public IStreamMonitor getOutputStreamMonitor() {
		return fOutputStreamMonitor;
	}

	public void write(String input) throws IOException {
		// Do nothing
	}

	public void message(String message) {
		fOutputStreamMonitor.append(message);
	}
	
	public void error(String message) {
		fOutputStreamMonitor.append(message);
	}

}
