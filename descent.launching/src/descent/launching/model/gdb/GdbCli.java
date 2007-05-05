package descent.launching.model.gdb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IRegister;
import org.eclipse.debug.core.model.IRegisterGroup;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IStreamsProxy;
import org.eclipse.debug.core.model.IVariable;

import descent.launching.model.ICli;
import descent.launching.model.ICliRequestor;
import descent.launching.model.IDescentDebugElementFactory;
import descent.launching.model.IDescentVariable;

public class GdbCli implements ICli {
	
	private final static boolean DEBUG = false;
	
	private int fTimeout;
	private boolean fshowBaseMembersInSameLevel;
	
	ICliRequestor fCliRequestor;
	IDescentDebugElementFactory fFactory;
	
	private IState fState;
	private IState fRunningState = new Running(this);
	private IStreamsProxy fProxy;

	private Object fWaitLock = new Object();
	private volatile boolean fWaitLockUsed;
	
	public List<String> getDebuggerCommandLineArguments() {
		List<String> args = new ArrayList<String>();
		args.add("-readnow");
		args.add("-fullname");
		return args;
	}
	
	public List<String> getDebugeeCommandLineArguments(String[] arguments) {
		List<String> args = new ArrayList<String>();
		args.add("-args");
		args.addAll(Arrays.asList(arguments));
		return args;
	}

	public void addBreakpoint(IResource resource, int lineNumber) throws DebugException, IOException {
		try {
			setState(new AddingBreakpoint(this));
			
			beforeWaitStateReturn();
			
			fProxy.write("break ");
			fProxy.write(toGdbPath(resource.getLocation().toOSString()));
			fProxy.write(":");
			fProxy.write(String.valueOf(lineNumber));
			fProxy.write("\n");
			
			waitStateReturn();
		} finally {
			setState(fRunningState);
		}
	}

	public IDescentVariable evaluateExpression(int stackFrameNumber, String expression) throws IOException {
		// TODO
		return null;
	}

	public String getEndCommunicationString() {
		return "(gdb) ";
	}

	public byte[] getMemoryBlock(long startAddress, long length) throws IOException {
		// TODO
		return new byte[0];
	}

	public IRegister[] getRegisters(IRegisterGroup registerGroup) throws IOException {
		// TODO
		return new IRegister[0];
	}

	public IStackFrame[] getStackFrames() throws DebugException, IOException {
		if (DEBUG) {
			System.out.println("*getStackFrames()");
		}
		
		try {
			setState(new ConsultingStackFrames(this));
			
			beforeWaitStateReturn();
			
			fProxy.write("bt\n");
			
			waitStateReturn();
			
			List<IStackFrame> stackFrames = ((ConsultingStackFrames) fState).fStackFrames;
			return stackFrames.toArray(new IStackFrame[stackFrames.size()]);
		} finally {
			setState(fRunningState);
		}
	}

	public IVariable[] getVariables(int stackFrameNumber) throws IOException {
		// TODO
		return new IVariable[0];
	}

	public void initialize(ICliRequestor requestor, IDescentDebugElementFactory factory, IStreamsProxy out, int timeout, boolean showBaseMembersInSameLevel) {
		this.fCliRequestor = requestor;
		this.fFactory = factory;
		this.fProxy = out;
		this.fTimeout = timeout;
		this.fshowBaseMembersInSameLevel = showBaseMembersInSameLevel;
	}

	public void interpret(String text) throws DebugException, IOException {
		if (DEBUG) {
			System.out.println("~" + text);
		}
		
		fState.interpret(text);
	}

	public boolean isSingleThread() {
		return true;
	}

	public void removeBreakpoint(IResource resource, int lineNumber) throws DebugException, IOException {
		try {
			setState(new RemovingBreakpoint(this));
			
			beforeWaitStateReturn();
			
			fProxy.write("clear ");
			fProxy.write(toGdbPath(resource.getLocation().toOSString()));
			fProxy.write(":");
			fProxy.write(String.valueOf(lineNumber));
			fProxy.write("\n");
			
			waitStateReturn();
		} finally {
			setState(fRunningState);
		}
	}

	public void resume() throws DebugException, IOException {
		try {
			fProxy.write("c\n");
		} finally {
			setState(fRunningState);
		}
	}

	public void setStackFrame(int stackFrame) throws DebugException, IOException {
		try {
			setState(new SettingStackFrame(this));
			
			beforeWaitStateReturn();
			
			fProxy.write("frame ");
			fProxy.write(String.valueOf(stackFrame));
			fProxy.write("\n");
			
			waitStateReturn();
		} finally {
			setState(fRunningState);
		}
	}

	public void start() throws DebugException, IOException {
		try {
			fProxy.write("r\n");
		} finally {
			setState(fRunningState);
		}
	}

	public void stepOver() throws IOException {
		step("next", DebugEvent.STEP_OVER);
	}

	public void stepInto() throws IOException {
		step("step", DebugEvent.STEP_INTO);
	}

	public void stepReturn() throws IOException {
		step("finish", DebugEvent.STEP_RETURN);
	}
	
	private void step(String cmd, int debugEvent) throws IOException {
		try {
			setState(new Stepping(this, debugEvent));
			
			beforeWaitStateReturn();
			
			fProxy.write(cmd + "\n");
			
			waitStateReturn();
		} finally {
			setState(fRunningState);
		}
	}

	public void terminate() throws DebugException, IOException {
		try {
			fProxy.write("quit\n");
			fProxy.write("y\n");
		} finally {
			setState(fRunningState);
		}
	}
	
	private String toGdbPath(String path) {
		return path.replace('\\', '/');
	}
	
	void setState(IState state) {
		this.fState = state;
	}
	
	void beforeWaitStateReturn() {
		fWaitLockUsed = false;
	}
	
	void waitStateReturn() {
		try {
			synchronized (fWaitLock) {
				if (!fWaitLockUsed) {
					fWaitLock.wait(fTimeout);
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	void notifyStateReturn() {
		fWaitLockUsed = true;
		synchronized (fWaitLock) {
			fWaitLock.notify();
		}		
	}

}
