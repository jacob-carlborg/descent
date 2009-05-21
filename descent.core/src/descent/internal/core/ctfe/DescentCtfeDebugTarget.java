package descent.internal.core.ctfe;

import java.io.IOException;

import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IMemoryBlock;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IThread;

public class DescentCtfeDebugTarget extends DescentCtfeDebugElement implements IDebugTarget {

	private final ILaunch fLaunch;
	private final IProcess fProcess;
	private boolean fSuspended;

	public DescentCtfeDebugTarget(ILaunch launch, IProcess process) {
		super(null);
		this.fLaunch = launch;
		this.fProcess = process;
	}

	public String getName() throws DebugException {
		return "Descent Compile-Time";
	}

	public IProcess getProcess() {
		return fProcess;
	}

	public IThread[] getThreads() throws DebugException {
		return new IThread[0];
	}

	public boolean hasThreads() throws DebugException {
		return false;
	}

	public boolean supportsBreakpoint(IBreakpoint breakpoint) {
		return true;
	}

	public IDebugTarget getDebugTarget() {
		return this;
	}

	public ILaunch getLaunch() {
		return fLaunch;
	}

	public boolean canTerminate() {
		return fProcess.canTerminate();
	}

	public boolean isTerminated() {
		return fProcess.isTerminated();
	}

	public void terminate() throws DebugException {
		if (isTerminated()) return;
		
//		try {
//			fDebugger.terminate();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
		internalTerminate();
	}
	
	private void internalTerminate() {
//		fThread.fireTerminateEvent();
		
//		fProcess.getStreamsProxy().getOutputStreamMonitor().removeListener(fOutStreamListener);		
//		fProcess.getStreamsProxy().getErrorStreamMonitor().removeListener(fErrorStreamListener);
//		DebugPlugin.getDefault().getBreakpointManager().removeBreakpointListener(this);
	}

	public boolean canResume() {
		return !isTerminated() && isSuspended();
	}

	public boolean canSuspend() {
		return false;
	}

	public boolean isSuspended() {
		return fSuspended;
	}

	public void resume() throws DebugException {
		if (isTerminated()) return;
		
		fireResumeEvent(DebugEvent.CLIENT_REQUEST);
		
//		try {
			fSuspended = false;
//			fDebugger.resume();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}

	public void suspend() throws DebugException {
	}

	public void breakpointAdded(IBreakpoint breakpoint) {
		// TODO Auto-generated method stub
		
	}

	public void breakpointChanged(IBreakpoint breakpoint, IMarkerDelta delta) {
		// TODO Auto-generated method stub
		
	}

	public void breakpointRemoved(IBreakpoint breakpoint, IMarkerDelta delta) {
		// TODO Auto-generated method stub
		
	}

	public boolean canDisconnect() {
		return false;
	}

	public void disconnect() throws DebugException {
	}

	public boolean isDisconnected() {
		return false;
	}

	public IMemoryBlock getMemoryBlock(long startAddress, long length)
			throws DebugException {
		return null;
	}

	public boolean supportsStorageRetrieval() {
		return false;
	}

}
