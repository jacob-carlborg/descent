package descent.internal.core.ctfe;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamsProxy;

public class CtfeProcess implements IProcess {
	
	private final ILaunch launch;
	private boolean terminated;

	public CtfeProcess(ILaunch launch) {
		this.launch = launch;
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
		return null;
	}

	public boolean canTerminate() {
		return true;
	}

	public boolean isTerminated() {
		return terminated;
	}

	public void terminate() throws DebugException {
		this.terminated = true;
	}

}
