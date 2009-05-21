package descent.internal.core.ctfe;

import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointManager;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.ILineBreakpoint;
import org.eclipse.debug.core.model.IMemoryBlock;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IThread;

public class DescentCtfeDebugTarget extends DescentCtfeDebugElement implements IDebugTarget {

	private final ILaunch fLaunch;
	private final IProcess fProcess;
	private final IDebugger fDebugger;
	
	private boolean fSuspended;
	private DescentCtfeThread fThread;
	private DescentCtfeThread[] fThreads;

	public DescentCtfeDebugTarget(ILaunch launch, IProcess process, IDebugger debugger) {
		super(null);
		this.fLaunch = launch;
		this.fProcess = process;
		this.fDebugger = debugger;
		
		this.fThread = new DescentCtfeThread(this, fDebugger);
		this.fThreads = new DescentCtfeThread[] { fThread };
		
		DebugPlugin.getDefault().getBreakpointManager().addBreakpointListener(this);
	}
	
	/**
	 * Adds the currently defined breakpoints to the debugger
	 * interpreter, and starts the session.
	 */
	public void started() throws DebugException {
		fireCreationEvent();
		fThread.fireCreationEvent();
		
		installDeferredBreakpoints();
		
		fireResumeEvent(DebugEvent.CLIENT_REQUEST);
		
		fDebugger.start();
		fSuspended = false;
	}
	
	protected void installDeferredBreakpoints() {
		try {
			IBreakpointManager manager = DebugPlugin.getDefault().getBreakpointManager();
			IBreakpoint[] breakpoints = manager.getBreakpoints("descent.debug.core.model");
			for(IBreakpoint breakpoint : breakpoints) {
				if (breakpoint.isEnabled()) {
					fDebugger.addBreakpoint(breakpoint.getMarker().getResource(), ((ILineBreakpoint) breakpoint).getLineNumber());
				}
			}
		} catch (CoreException e) {
		}
	}

	public String getName() throws DebugException {
		return "Descent Compile-Time";
	}

	public IProcess getProcess() {
		return fProcess;
	}

	public IThread[] getThreads() throws DebugException {
		return fThreads;
	}

	public boolean hasThreads() throws DebugException {
		return true;
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
		
		fProcess.terminate();
		fDebugger.terminate();
		
		internalTerminate();
	}
	
	private void internalTerminate() {
		fThread.fireTerminateEvent();
		
//		fProcess.getStreamsProxy().getOutputStreamMonitor().removeListener(fOutStreamListener);		
//		fProcess.getStreamsProxy().getErrorStreamMonitor().removeListener(fErrorStreamListener);
		DebugPlugin.getDefault().getBreakpointManager().removeBreakpointListener(this);
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
		if (isTerminated()) return;
		
		try {
			fDebugger.addBreakpoint(breakpoint.getMarker().getResource(), ((ILineBreakpoint) breakpoint).getLineNumber());
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	public void breakpointChanged(IBreakpoint breakpoint, IMarkerDelta delta) {
		if (supportsBreakpoint(breakpoint)) {
			try {
				if (breakpoint.isEnabled()) {
					breakpointAdded(breakpoint);
				} else {
					breakpointRemoved(breakpoint, null);
				}
			} catch (CoreException e) {
			}
		}
	}

	public void breakpointRemoved(IBreakpoint breakpoint, IMarkerDelta delta) {
		if (isTerminated()) return;
		
		try {
			fDebugger.removeBreakpoint(breakpoint.getMarker().getResource(), ((ILineBreakpoint) breakpoint).getLineNumber());
		} catch (CoreException e) {
			e.printStackTrace();
		}
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

	public void stepInto() {
		// TODO Auto-generated method stub
		
	}

	public void stepOver() {
		// TODO Auto-generated method stub
		
	}

	public void stepReturn() {
		// TODO Auto-generated method stub
		
	}
	
	public void breakpointHit(IResource resource, int lineNumber) throws DebugException {
		fSuspended = true;
		
		if (resource != null) {
			IBreakpoint breakpoint = findBreakpoint(resource, lineNumber);
			if (breakpoint != null) {
				fThread.setBreakpoints(new IBreakpoint[] { breakpoint });
			}
		}
		
		fThread.invalidateChildren();
		fThread.fireSuspendEvent(DebugEvent.BREAKPOINT);
	}
	
	private IBreakpoint findBreakpoint(IResource resource, int lineNumber) {
		IBreakpoint[] breakpoints = DebugPlugin.getDefault().getBreakpointManager().getBreakpoints("descent.debug.core.model");
		for (int i = 0; i < breakpoints.length; i++) {
			IBreakpoint breakpoint = breakpoints[i];
			if (supportsBreakpoint(breakpoint)) {
				if (breakpoint instanceof ILineBreakpoint) {
					ILineBreakpoint lineBreakpoint = (ILineBreakpoint) breakpoint;
					try {
						if (lineBreakpoint.getLineNumber() == lineNumber
								&& breakpoint.getMarker().getResource().equals(resource)) {
							return breakpoint;
						}
					} catch (CoreException e) {
					}
				}
			}
		}
		return null;
	}

}
