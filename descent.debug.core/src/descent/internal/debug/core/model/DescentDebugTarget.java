package descent.internal.debug.core.model;

import java.io.IOException;

import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IResource;
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
import org.eclipse.jface.preference.IPreferenceStore;

import descent.debug.core.DescentDebugPlugin;
import descent.debug.core.IDescentLaunchConfigurationConstants;
import descent.debug.core.IDescentLaunchingPreferenceConstants;
import descent.debug.core.model.IDebugElementFactory;
import descent.debug.core.model.IDebugger;
import descent.debug.core.model.IDebuggerListener;
import descent.debug.core.model.IParentVariable;

public class DescentDebugTarget extends DescentDebugElement implements IDebugTarget, IDebuggerListener, IDebugElementFactory {
	
	private ILaunch fLaunch;
	private IProcess fProcess;
	private String fName;
	
	private IDebugger fDebugger;
	private String fEndCommunicationString;
	
	private DescentThread fThread;
	private DescentThread[] fThreads;
	
	private IStreamListener fOutStreamListener;
	private IStreamListener fErrorStreamListener;
	
	private boolean fSuspended;
	
	public DescentDebugTarget(ILaunch launch, IProcess process, IDebugger debugger) {
		super(null);
		this.fLaunch = launch;
		this.fProcess = process;
		this.fDebugger = debugger;
		this.fEndCommunicationString = fDebugger.getEndCommunicationString().trim();
		
		// Force sending one request at a time
		this.fDebugger = new SingleThreadDebugger(this.fDebugger);
		
		IPreferenceStore preferenceStore = DescentDebugPlugin.getDefault().getPreferenceStore();
		int timeout = preferenceStore.getInt(IDescentLaunchingPreferenceConstants.DEBUGGER_TIMEOUT);
		boolean showBaseMembersInSameLevel = preferenceStore.getBoolean(IDescentLaunchingPreferenceConstants.SHOW_BASE_MEMBERS_IN_SAME_LEVEL);
		this.fDebugger.initialize(this, this, process.getStreamsProxy(), timeout, showBaseMembersInSameLevel);
		
		this.fThread = new DescentThread(this, fDebugger);
		this.fThreads = new DescentThread[] { fThread };
		
		this.fOutStreamListener = new MyStreamListener(false);
		this.fErrorStreamListener = new MyStreamListener(true);
		
		process.getStreamsProxy().getOutputStreamMonitor().addListener(fOutStreamListener);
		process.getStreamsProxy().getErrorStreamMonitor().addListener(fErrorStreamListener);
		
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
				if (breakpoint.isEnabled()) {
					fDebugger.addBreakpoint(getFilename(breakpoint), ((ILineBreakpoint) breakpoint).getLineNumber());
				}
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
		return true;
	}

	public boolean canTerminate() {
		return fProcess.canTerminate();
	}

	public boolean isTerminated() {
		return fProcess.isTerminated();
	}
	
	public void suspend() throws DebugException {
		System.out.println("suspend()");
	}

	public void terminate() throws DebugException {
		if (isTerminated()) return;
		
		try {
			fDebugger.terminate();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		internalTerminate();
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
		
		try {
			fSuspended = false;
			fDebugger.resume();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void breakpointAdded(IBreakpoint breakpoint) {
		if (isTerminated()) return;
		
		try {
			fDebugger.addBreakpoint(getFilename(breakpoint), ((ILineBreakpoint) breakpoint).getLineNumber());
		} catch (IOException e) {
			e.printStackTrace();
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
			fDebugger.removeBreakpoint(getFilename(breakpoint), ((ILineBreakpoint) breakpoint).getLineNumber());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
	
	private String getFilename(IBreakpoint breakpoint) {
		IResource resource = breakpoint.getMarker().getResource();
		return resource.getLocation().toOSString();
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
		if (isTerminated()) return null;
		
		return new DescentMemoryBlock(this, fDebugger, startAddress, length);
	}

	public boolean supportsStorageRetrieval() {
		return true;
	}
	
	public IStackFrame[] getStackFrames() {
		try {
			return fDebugger.getStackFrames();
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
			fDebugger.stepOver();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void stepInto() {
		try {
			fDebugger.stepInto();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void stepReturn() {
		try {
			fDebugger.stepReturn();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	class MyStreamListener implements IStreamListener {
		
		private StringBuilder fStreamBuffer;
		private boolean fIsError;
		
		public MyStreamListener(boolean isError) {
			this.fIsError = isError;
			this.fStreamBuffer = new StringBuilder();
		}
		
		public void streamAppended(String text, IStreamMonitor monitor) {
			try {
				fStreamBuffer.append(text);
				
				if (fStreamBuffer.toString().trim().equals(fEndCommunicationString)) {
					if (fIsError) {
						fDebugger.interpretError(fEndCommunicationString);
					} else {
						fDebugger.interpret(fEndCommunicationString);
					}
					fStreamBuffer.setLength(0);
				}
				
				int indexOfLine = fStreamBuffer.indexOf("\n"); //$NON-NLS-1$
				if (indexOfLine == -1) return;
				
				text = fStreamBuffer.toString();
				
				int lastIndexOfLine = 0;
				
				while(indexOfLine != -1) {
					String line = fStreamBuffer.substring(lastIndexOfLine, indexOfLine);
					if (fIsError) {
						fDebugger.interpretError(line);
					} else {
						fDebugger.interpret(line);
					}
					
					lastIndexOfLine = indexOfLine + 1;
					indexOfLine = fStreamBuffer.indexOf("\n", lastIndexOfLine); //$NON-NLS-1$
				}
				
				fStreamBuffer.delete(0, lastIndexOfLine);
				
				String reminder = fStreamBuffer.toString().trim();
				
				if (reminder.equals(fEndCommunicationString)) {
					if (fIsError) {
						fDebugger.interpretError(fEndCommunicationString);
					} else {
						fDebugger.interpret(fEndCommunicationString);
					}
					fStreamBuffer.setLength(0);
					
				// Special case, may happen
				} else if (reminder.endsWith(fEndCommunicationString)) {
					int index = reminder.length() - fEndCommunicationString.length();
					if (fIsError) {
						fDebugger.interpretError(reminder.substring(0, index));
						fDebugger.interpretError(fEndCommunicationString);
					} else {
						fDebugger.interpret(reminder.substring(0, index));
						fDebugger.interpret(fEndCommunicationString);
					}
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
		
		try {
			fDebugger.start();
			fSuspended = false;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void stepEnded() {
		fSuspended = true;
		fThread.setBreakpoints(null);
		fThread.invalidateChildren();
		fThread.fireSuspendEvent(DebugEvent.STEP_END);
	}
	
	public void breakpointHit() throws DebugException {
		breakpointHit(null, -1);
	}
	
	public void breakpointHit(String fileName, int lineNumber) throws DebugException {
		fSuspended = true;
		
		if (fileName != null) {
			IBreakpoint breakpoint = findBreakpoint(fileName, lineNumber);
			if (breakpoint != null) {
				fThread.setBreakpoints(new IBreakpoint[] { breakpoint });
			}
		}
		
		fThread.invalidateChildren();
		fThread.fireSuspendEvent(DebugEvent.BREAKPOINT);
	}
	
	private IBreakpoint findBreakpoint(String fileName, int lineNumber) {
		IBreakpoint[] breakpoints = DebugPlugin.getDefault().getBreakpointManager().getBreakpoints(IDescentLaunchConfigurationConstants.ID_D_DEBUG_MODEL);
		for (int i = 0; i < breakpoints.length; i++) {
			IBreakpoint breakpoint = breakpoints[i];
			if (supportsBreakpoint(breakpoint)) {
				if (breakpoint instanceof ILineBreakpoint) {
					ILineBreakpoint lineBreakpoint = (ILineBreakpoint) breakpoint;
					try {
						if (lineBreakpoint.getLineNumber() == lineNumber) {
							return breakpoint;
						}
					} catch (CoreException e) {
					}
				}
			}
		}
		return null;
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
			terminate();
		} catch (DebugException e) {
			e.printStackTrace();
		}
	}
	
	private void internalTerminate() {
		fThread.fireTerminateEvent();
		
		fProcess.getStreamsProxy().getOutputStreamMonitor().removeListener(fOutStreamListener);		
		fProcess.getStreamsProxy().getErrorStreamMonitor().removeListener(fErrorStreamListener);
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
		return new DescentRegister(this, fDebugger, registerGroup, name, value);
	}

	public IParentVariable newParentVariable(int stackFrame, String name, String value) {
		return new DescentVariable(this, fDebugger, stackFrame, name, value);
	}
	
	public IParentVariable newLazyVariable(int stackFrame, String name, String value, String expression) {
		return new DescentVariable(this, fDebugger, stackFrame, name, value, expression);
	}
	
	public IStackFrame newStackFrame(String name, int number, String sourceName, int lineNumber) {
		return new DescentStackFrame(this, fDebugger, fThreads[0], name, number, sourceName, lineNumber);
	}
	
}
