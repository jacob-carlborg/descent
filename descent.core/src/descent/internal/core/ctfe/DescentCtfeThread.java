package descent.internal.core.ctfe;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;

public class DescentCtfeThread extends DescentCtfeDebugElement implements IThread  {
	
	/**
	 * Breakpoints this thread is suspended at or <code>null</code>
	 * if none.
	 */
	private IBreakpoint[] fBreakpoints;
	
	/**
	 * Whether this thread is stepping
	 */
	private boolean fStepping = false;
	private boolean fStackFramesInvalid;
	private IStackFrame[] fStackFrames;
	
	private final ICtfeDebugger fDebugger;
	
	public DescentCtfeThread(DescentCtfeDebugTarget target, ICtfeDebugger debugger) {
		super(target);
		this.fDebugger = debugger;
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

	public IBreakpoint[] getBreakpoints() {
		if (fBreakpoints == null) {
			return new IBreakpoint[0];
		}
		return fBreakpoints;
	}

	public String getName() throws DebugException {
		return "main";
	}

	public int getPriority() throws DebugException {
		return 0;
	}

	public IStackFrame[] getStackFrames() throws DebugException {
		if (isSuspended() && !isTerminated()) {
			if (fStackFramesInvalid) {
				IStackFrame[] newStackFrames = ((DescentCtfeDebugTarget)getDebugTarget()).getStackFrames(); 
				fStackFrames = mergeStackFrames(fStackFrames, newStackFrames);	
				fStackFramesInvalid = false;
				fireChangeEvent(DebugEvent.CONTENT);
			}
			return fStackFrames;
		} else {
			return new IStackFrame[0];
		}
	}
	
	private IStackFrame[] mergeStackFrames(IStackFrame[] stackFrames, IStackFrame[] newStackFrames) {
		if (stackFrames == null || (stackFrames.length != newStackFrames.length)) {
			return newStackFrames;
		}
		for(int i = 0; i < stackFrames.length; i++) {
			DescentCtfeStackFrame oldSF = (DescentCtfeStackFrame) stackFrames[i];
			DescentCtfeStackFrame newSF = (DescentCtfeStackFrame) newStackFrames[i];
			if (oldSF.isInSameFunction(newSF)) {
				oldSF.merge(newSF);
			} else {
				stackFrames[i] = newSF;
			}
		}
		return stackFrames;
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
		return getDebugTarget().canSuspend();
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
		((DescentCtfeDebugTarget)getDebugTarget()).stepInto();
	}

	public void stepOver() throws DebugException {
		((DescentCtfeDebugTarget)getDebugTarget()).stepOver();
	}

	public void stepReturn() throws DebugException {
		((DescentCtfeDebugTarget)getDebugTarget()).stepReturn();
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
	
	public void invalidateChildren() {
		invalidateStackFrames();
	}
	
	private void invalidateStackFrames() {
		fStackFramesInvalid = true;
	}

}
