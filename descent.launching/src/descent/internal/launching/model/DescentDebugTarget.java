package descent.internal.launching.model;

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
import org.eclipse.debug.core.model.IRegister;
import org.eclipse.debug.core.model.IRegisterGroup;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IStreamMonitor;
import org.eclipse.debug.core.model.IThread;

import descent.launching.IDescentLaunchConfigurationConstants;
import descent.launching.model.ICli;
import descent.launching.model.ICliRequestor;
import descent.launching.model.IDescentDebugElementFactory;
import descent.launching.model.IDescentVariable;
import descent.launching.model.ddbg.DdbgCli;

public class DescentDebugTarget extends DescentDebugElement implements IDebugTarget, IStreamListener, ICliRequestor, IDescentDebugElementFactory {
	
	private ILaunch fLaunch;
	private IProcess fProcess;
	private String fName;
	
	private ICli fInterpreter;
	private String fEndCommunicationString;
	
	private DescentThread fThread;
	private DescentThread[] fThreads;
	
	private StringBuilder fStreamBuffer;
	
	private boolean fSuspended;
	
	public DescentDebugTarget(ILaunch launch, IProcess process) {
		super(null);
		this.fLaunch = launch;
		this.fProcess = process;
		this.fThread = new DescentThread(this);
		this.fThreads = new DescentThread[] { fThread };
		this.fInterpreter = new DdbgCli();
		this.fEndCommunicationString = fInterpreter.getEndCommunicationString();
		
		// If it's a single thread interpreter, 
		// force sending one request at a time
		if (this.fInterpreter.isSingleThread()) {
			this.fInterpreter = new SingleThreadCli(this.fInterpreter);
		}
		
		this.fInterpreter.initialize(this, this, process.getStreamsProxy());
		this.fStreamBuffer = new StringBuilder();
		
		IStreamMonitor out = process.getStreamsProxy().getOutputStreamMonitor();
		out.addListener(this);
		
		DebugPlugin.getDefault().getBreakpointManager().addBreakpointListener(this);
	}
	
	@Override
	public IDebugTarget getDebugTarget() {
		return this;
	}
	
	protected void installDeferredBreakpoints() {
		try {
			IBreakpointManager manager = DebugPlugin.getDefault().getBreakpointManager();
			IBreakpoint[] breakpoints = manager.getBreakpoints(IDescentLaunchConfigurationConstants.ID_D_DEBUG_MODEL);
			for(IBreakpoint breakpoint : breakpoints) {
				fInterpreter.addBreakpoint(breakpoint.getMarker().getResource(), ((ILineBreakpoint) breakpoint).getLineNumber());
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
		if (isTerminated()) return;
		
		try {
			fInterpreter.terminate();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		internalTerminate();
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
		if (isTerminated()) return;
		
		try {
			fSuspended = false;
			fInterpreter.resume();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		fireResumeEvent(DebugEvent.CLIENT_REQUEST);
	}
	
	public void suspend() throws DebugException {
		
	}

	public void breakpointAdded(IBreakpoint breakpoint) {
		if (isTerminated()) return;
		
		try {
			fInterpreter.addBreakpoint(breakpoint.getMarker().getResource(), ((ILineBreakpoint) breakpoint).getLineNumber());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	public void breakpointChanged(IBreakpoint breakpoint, IMarkerDelta delta) {
	}

	public void breakpointRemoved(IBreakpoint breakpoint, IMarkerDelta delta) {
		if (isTerminated()) return;
		
		try {
			fInterpreter.removeBreakpoint(breakpoint.getMarker().getResource(), ((ILineBreakpoint) breakpoint).getLineNumber());
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
			return fInterpreter.getStackFrames();
		} catch (IOException e) {
			e.printStackTrace();
			return new IStackFrame[0];
		} catch (DebugException e) {
			e.printStackTrace();
			return new IStackFrame[0];
		}
	}
	
	public void stepOver() {
		try {
			fInterpreter.stepOver();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void stepInto() {
		try {
			fInterpreter.stepInto();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void stepReturn() {
		try {
			fInterpreter.stepReturn();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void streamAppended(String text, IStreamMonitor monitor) {
		try {
			fStreamBuffer.append(text);
			
			if (fStreamBuffer.toString().equals(fEndCommunicationString)) {
				fInterpreter.interpret(fEndCommunicationString);
				fStreamBuffer.setLength(0);
			}
			
			int indexOfLine = fStreamBuffer.indexOf("\n");
			if (indexOfLine == -1) return;
			
			text = fStreamBuffer.toString();
			
			int lastIndexOfLine = 0;
			
			while(indexOfLine != -1) {
				String line = fStreamBuffer.substring(lastIndexOfLine, indexOfLine);
				fInterpreter.interpret(line);
				
				lastIndexOfLine = indexOfLine + 1;
				indexOfLine = fStreamBuffer.indexOf("\n", lastIndexOfLine);
			}
			
			fStreamBuffer.delete(0, lastIndexOfLine);
			
			if (fStreamBuffer.toString().equals(fEndCommunicationString)) {
				fInterpreter.interpret(fEndCommunicationString);
				fStreamBuffer.setLength(0);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (DebugException e) {
			e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Adds the currently defined breakpoints to the debugger
	 * interpreter, and starts the session.
	 */
	public void started() throws DebugException {
		fireCreationEvent();
		fThread.fireCreationEvent();
		installDeferredBreakpoints();
		resume();
	}
	
	public void suspended(int detail) {
		fSuspended = true;
		fThread.invalidateStackFrames();
		fThread.fireSuspendEvent(detail);
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
	
	public void terminated() {
		try {
			fInterpreter.terminate();
		} catch (DebugException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		internalTerminate();
	}
	
	private void internalTerminate() {
		fThread.fireTerminateEvent();
		
		fProcess.getStreamsProxy().getOutputStreamMonitor().removeListener(this);		
		DebugPlugin.getDefault().getBreakpointManager().removeBreakpointListener(this);
	}
	
	@Override
	public String toString() {
		try {
			return getName();
		} catch (DebugException e) {
			return super.toString();
		}
	}

	public IRegister newRegister(IRegisterGroup registerGroup, String name, String value) {
		return new DescentRegister(this, registerGroup, name, value);
	}

	public IDescentVariable newVariable(String name, String value) {
		return new DescentVariable(this, name, value);
	}
	
	public IStackFrame newStackFrame(String name, int number, String sourceName, int lineNumber) {
		return new DescentStackFrame(this, fInterpreter, fThreads[0], name, number, sourceName, lineNumber);
	}
	
}
