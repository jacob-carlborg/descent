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
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;

import descent.core.ICompilationUnit;
import descent.core.ctfe.IDebugger;
import descent.internal.compiler.parser.InterState;
import descent.internal.compiler.parser.Scope;

public class DescentDebugTarget extends DescentDebugElement implements IDebugTarget {

	private final ILaunch fLaunch;
	private final IProcess fProcess;
	private final IDebugger fDebugger;
	
	private boolean fSuspended;
	private DescentThread fThread;
	private DescentThread[] fThreads;

	public DescentDebugTarget(ILaunch launch, IProcess process, IDebugger debugger) {
		super(null);
		this.fLaunch = launch;
		this.fProcess = process;
		this.fDebugger = debugger;
		
		this.fThread = new DescentThread(this);
		this.fThreads = new DescentThread[] { fThread };
		
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
		
		fSuspended = false;
		fDebugger.resume();
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
		fDebugger.stepInto();
	}

	public void stepOver() {
		fDebugger.stepOver();
	}

	public void stepReturn() {
		fDebugger.stepReturn();
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

	public IStackFrame[] getStackFrames() {
		return fDebugger.getStackFrames();
	}
	
	public DescentStackFrame newStackFrame(String name, int number, ICompilationUnit unit, int lineNumber, Scope sc, InterState is) {
		return new DescentStackFrame(this, fDebugger, fThreads[0], name, number, unit, lineNumber, sc, is);
	}
	
	public void stepEnded() {
		fSuspended = true;
		fThread.setBreakpoints(null);
		fThread.invalidateChildren();
		fThread.fireSuspendEvent(DebugEvent.STEP_END);
	}
	
	public void breakpointHit(ICompilationUnit unit, int lineNumber) throws DebugException {
		fSuspended = true;
		
		if (unit.getResource() != null) {
			IBreakpoint breakpoint = findBreakpoint(unit.getResource(), lineNumber);
			if (breakpoint != null) {
				fThread.setBreakpoints(new IBreakpoint[] { breakpoint });
			}
		}
		
		fThread.invalidateChildren();
		fThread.fireSuspendEvent(DebugEvent.BREAKPOINT);
	}
	
	public void resumed(int detail) {
		fSuspended = false;
		
		if ((detail & DebugEvent.STEP_INTO) != 0 ||
				(detail & DebugEvent.STEP_OVER) != 0 ||
				(detail & DebugEvent.STEP_RETURN) != 0) {
			fThread.setStepping(true);
		} else {
			fThread.setStepping(false);
		}
		
		fThread.fireResumeEvent(detail);
	}

}
