package descent.launching.model;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;

public class DescentThread extends DescentDebugElement implements IThread {
	
	/**
	 * Breakpoints this thread is suspended at or <code>null</code>
	 * if none.
	 */
	private IBreakpoint[] fBreakpoints;
	
	/**
	 * Whether this thread is stepping
	 */
	private boolean fStepping = false;
	
	public DescentThread(DescentDebugTarget target) {
		super(target);
	}

	public IBreakpoint[] getBreakpoints() {
		if (fBreakpoints == null) {
			return new IBreakpoint[0];
		}
		return fBreakpoints;
	}
	
	/**
	 * Sets the breakpoints this thread is suspended at, or <code>null</code>
	 * if none.
	 * 
	 * @param breakpoints the breakpoints this thread is suspended at, or <code>null</code>
	 * if none
	 */
	protected void setBreakpoints(IBreakpoint[] breakpoints) {
		fBreakpoints = breakpoints;
	}

	public String getName() throws DebugException {
		return "Thread[main]";
	}

	public int getPriority() throws DebugException {
		return 0;
	}

	public IStackFrame[] getStackFrames() throws DebugException {
		if (isSuspended() && !isTerminated()) {
			return ((DescentDebugTarget)getDebugTarget()).getStackFrames();
		} else {
			return new IStackFrame[0];
		}
	}

	public IStackFrame getTopStackFrame() throws DebugException {
		IStackFrame[] frames = getStackFrames();
		if (frames.length > 0) {
			return frames[0];
		}
		return null;
	}

	public boolean hasStackFrames() throws DebugException {
		return isSuspended();
	}

	public boolean canResume() {
		return isSuspended();
	}

	public boolean canSuspend() {
		return !isTerminated() && !isSuspended();
	}

	public boolean isSuspended() {
		return getDebugTarget().isSuspended();
	}

	public void resume() throws DebugException {
		getDebugTarget().resume();
	}

	public void suspend() throws DebugException {
		getDebugTarget().suspend();
	}

	public boolean canStepInto() {
		return isSuspended();
	}

	public boolean canStepOver() {
		return isSuspended();
	}

	public boolean canStepReturn() {
		return isSuspended();
	}
	
	/**
	 * Sets whether this thread is stepping
	 * 
	 * @param stepping whether stepping
	 */
	protected void setStepping(boolean stepping) {
		fStepping = stepping;
	}

	public boolean isStepping() {
		return fStepping;
	}

	public void stepInto() throws DebugException {
		((DescentDebugTarget)getDebugTarget()).stepInto();
	}

	public void stepOver() throws DebugException {
		((DescentDebugTarget)getDebugTarget()).stepOver();
	}

	public void stepReturn() throws DebugException {
		((DescentDebugTarget)getDebugTarget()).stepReturn();
	}

	public boolean canTerminate() {
		return !isTerminated();
	}

	public boolean isTerminated() {
		return getDebugTarget().isTerminated();
	}

	public void terminate() throws DebugException {
		getDebugTarget().terminate();
	}

}
