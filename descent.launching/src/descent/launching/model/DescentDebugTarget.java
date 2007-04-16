package descent.launching.model;

import java.io.IOException;

import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointManager;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.IStreamListener;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.ILineBreakpoint;
import org.eclipse.debug.core.model.IMemoryBlock;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IStreamMonitor;
import org.eclipse.debug.core.model.IThread;

import descent.launching.IDescentLaunchConfigurationConstants;
import descent.launching.model.ddbg.DdbgInterpreter;

public class DescentDebugTarget extends DescentDebugElement implements IDebugTarget, IStreamListener {
	
	private ILaunch fLaunch;
	private IProcess fProcess;
	private String fName;
	
	private DdbgInterpreter fInterpreter;
	
	private DescentThread fThread;
	private DescentThread[] fThreads;
	
	private boolean fSuspended;
	
	public DescentDebugTarget(ILaunch launch, IProcess process) {
		super(null);
		this.fLaunch = launch;
		this.fProcess = process;
		this.fThread = new DescentThread(this);
		this.fThreads = new DescentThread[] { fThread };
		this.fInterpreter = new DdbgInterpreter(this, process.getStreamsProxy());
		
		process.getStreamsProxy().getOutputStreamMonitor().addListener(this);		
		DebugPlugin.getDefault().getBreakpointManager().addBreakpointListener(this);
	}
	
	@Override
	public IDebugTarget getDebugTarget() {
		return this;
	}
	
	/**
	 * Adds the currently defined breakpoints to the debugger
	 * interpreter, and starts the session.
	 */
	public void started() throws DebugException {
		fireCreationEvent();
		installDeferredBreakpoints();
		resume();
	}
	
	protected void installDeferredBreakpoints() {
		try {
			IBreakpointManager manager = DebugPlugin.getDefault().getBreakpointManager();
			IBreakpoint[] breakpoints = manager.getBreakpoints(IDescentLaunchConfigurationConstants.ID_D_DEBUG_MODEL);
			for(IBreakpoint breakpoint : breakpoints) {
				fInterpreter.addBreakpoint(breakpoint.getMarker().getResource(), ((ILineBreakpoint) breakpoint).getLineNumber(), fProcess.getStreamsProxy());
			}
		} catch (IOException e) {
		} catch (CoreException e) {
		}
	}

	public String getName() throws DebugException {
		if (fName == null) {
			fName = "Descent Program";
			try {
				fName = getLaunch().getLaunchConfiguration().getAttribute(IDescentLaunchConfigurationConstants.ATTR_PROGRAM_NAME, "Descent Program");
			} catch (CoreException e) {
			}
		}
		return fName;
	}

	public IProcess getProcess() {
		return fProcess;
	}
	
	@Override
	public ILaunch getLaunch() {
		return fLaunch;
	}

	public IThread[] getThreads() throws DebugException {
		return fThreads;
	}

	public boolean hasThreads() throws DebugException {
		return true;
	}

	public boolean supportsBreakpoint(IBreakpoint breakpoint) {
		return false;
	}

	public boolean canTerminate() {
		return fProcess.canTerminate();
	}

	public boolean isTerminated() {
		return fProcess.isTerminated();
	}

	public void terminate() throws DebugException {
		try {
			fInterpreter.terminate();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		fProcess.getStreamsProxy().getOutputStreamMonitor().removeListener(this);		
		DebugPlugin.getDefault().getBreakpointManager().removeBreakpointListener(this);
	}

	public boolean canResume() {
		return !isTerminated() && isSuspended();
	}

	public boolean canSuspend() {
		return !isTerminated() && !isSuspended();
	}

	public boolean isSuspended() {
		return fSuspended;
	}

	public void resume() throws DebugException {
		try {
			fSuspended = false;
			fInterpreter.resume();			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void suspend() throws DebugException {
		
	}

	public void breakpointAdded(IBreakpoint breakpoint) {
		try {
			fInterpreter.addBreakpoint(breakpoint.getMarker().getResource(), ((ILineBreakpoint) breakpoint).getLineNumber(), fProcess.getStreamsProxy());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	public void breakpointChanged(IBreakpoint breakpoint, IMarkerDelta delta) {
	}

	public void breakpointRemoved(IBreakpoint breakpoint, IMarkerDelta delta) {
		try {
			fInterpreter.removeBreakpoint(breakpoint.getMarker().getResource(), ((ILineBreakpoint) breakpoint).getLineNumber(), fProcess.getStreamsProxy());
		} catch (IOException e) {
			e.printStackTrace();
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

	public IMemoryBlock getMemoryBlock(long startAddress, long length) throws DebugException {
		return null;
	}

	public boolean supportsStorageRetrieval() {
		return false;
	}
	
	public IStackFrame[] getStackFrames() {
		try {
			return fInterpreter.getStackFrames(fThread);
		} catch (IOException e) {
			e.printStackTrace();
			return new IStackFrame[0];
		}
	}
	
	public void stepOver() {
		try {
			fInterpreter.stepOver();
			fThread.fireSuspendEvent(DebugEvent.STEP_OVER);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void stepInto() {
		try {
			fInterpreter.stepInto();
			fThread.fireSuspendEvent(DebugEvent.STEP_INTO);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void stepReturn() {
		try {
			fInterpreter.stepReturn();
			fThread.fireSuspendEvent(DebugEvent.STEP_RETURN);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void streamAppended(String text, IStreamMonitor monitor) {
		try {
			String[] lines = text.split("\n");
			for(String line : lines) {
				fInterpreter.interpret(line, this);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (DebugException e) {
			e.printStackTrace();
		}
	}
	
	public void suspended(int detail) {
		fSuspended = true;
		fireSuspendEvent(detail);
		fThread.fireSuspendEvent(detail);
	}
	
	public void resumed(int detail) {
		fSuspended = false;
		fireResumeEvent(detail);
		fThread.fireResumeEvent(detail);
	}
	
	public void steppedOver(int detail) {
		try {
			fInterpreter.stepOver();
			fThread.fireResumeEvent(DebugEvent.STEP_OVER);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void terminated() {
		try {
			terminate();
			fireTerminateEvent();
			fThread.fireTerminateEvent();
		} catch (DebugException e) {
			e.printStackTrace();
		}
	}	
	
}
