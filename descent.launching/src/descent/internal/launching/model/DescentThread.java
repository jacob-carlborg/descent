package descent.internal.launching.model;

import java.util.ArrayList;
import java.util.List;

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
	
	private List<IStackFrame> fStackFrames;
	
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
		StringBuilder sb = new StringBuilder();
		sb.append("Thread [main] (");
		if (isSuspended()) {
			sb.append("Suspended");
		} else if (isTerminated()) {
			sb.append("Terminated");
		} else {
			sb.append("Running");
		}
		sb.append(")");
		return sb.toString();
	}
	
	private String getStatus() {
		if (isSuspended()) return "Suspended";
		if (isTerminated()) return "Terminated";
		return "Running";
	}

	public int getPriority() throws DebugException {
		return 0;
	}

	public IStackFrame[] getStackFrames() throws DebugException {
		if (isSuspended() && !isTerminated()) {
			IStackFrame[] newStackFrames = ((DescentDebugTarget)getDebugTarget()).getStackFrames();
			fStackFrames = mergeStackFrames(fStackFrames, newStackFrames);
			return fStackFrames.toArray(new IStackFrame[fStackFrames.size()]);
		} else {
			return new IStackFrame[0];
		}
	}

	private List<IStackFrame> mergeStackFrames(List<IStackFrame> oldStackFrames, IStackFrame[] newStackFrames) {
		if (oldStackFrames == null) {
			List<IStackFrame> stackFrames = new ArrayList<IStackFrame>();
			for(IStackFrame stackFrame : newStackFrames) {
				stackFrames.add(stackFrame);
			}
			return stackFrames;
		} else {
			// TODO: change
			return oldStackFrames;
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
		return !isTerminated() && isSuspended();
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
		return !isTerminated() && isSuspended();
	}

	public boolean canStepOver() {
		return !isTerminated() && isSuspended();
	}

	public boolean canStepReturn() {
		return !isTerminated() && isSuspended();
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
	
	@Override
	public String toString() {
		try {
			return getName();
		} catch (DebugException e) {
			return super.toString();
		}
	}

}
