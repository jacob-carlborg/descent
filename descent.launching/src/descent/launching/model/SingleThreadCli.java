package descent.launching.model;

import java.io.IOException;

import org.eclipse.core.resources.IResource;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IRegister;
import org.eclipse.debug.core.model.IRegisterGroup;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IStreamsProxy;
import org.eclipse.debug.core.model.IVariable;

public class SingleThreadCli implements ICli {
	
	private final ICli fCli;
	private Object fLock;

	public SingleThreadCli(ICli cli) {
		this.fCli = cli;
		this.fLock = new Object();
	}
	
	public boolean isSingleThread() {
		return true;
	}

	public void addBreakpoint(IResource resource, int lineNumber) throws DebugException, IOException {
		synchronized (fLock) {
			fCli.addBreakpoint(resource, lineNumber);
		}
	}

	public IDescentVariable evaluateExpression(int stackFrameNumber, String expression) throws IOException {
		synchronized (fLock) {
			return fCli.evaluateExpression(stackFrameNumber, expression);
		}
	}

	public IRegister[] getRegisters(int stackFrameNumber, IRegisterGroup registerGroup) throws IOException {
		synchronized (fLock) {
			return fCli.getRegisters(stackFrameNumber, registerGroup);
		}
	}

	public IStackFrame[] getStackFrames() throws DebugException, IOException {
		synchronized (fLock) {
			return fCli.getStackFrames();
		}
	}

	public IVariable[] getVariables(int stackFrameNumber) throws IOException {
		synchronized (fLock) {
			return fCli.getVariables(stackFrameNumber);
		}
	}

	public void initialize(ICliRequestor requestor, IDescentDebugElementFactory factory, IStreamsProxy out) {
		fCli.initialize(requestor, factory, out);
	}

	public void interpret(String text) throws DebugException, IOException {
		fCli.interpret(text);
	}

	public void removeBreakpoint(IResource resource, int lineNumber) throws DebugException, IOException {
		synchronized (fLock) {
			fCli.removeBreakpoint(resource, lineNumber);
		}
	}

	public void resume() throws DebugException, IOException {
		synchronized (fLock) {
			fCli.resume();
		}
	}

	public void setStackFrame(int stackFrameNumber) throws DebugException, IOException {
		synchronized (fLock) {
			fCli.setStackFrame(stackFrameNumber);
		}
	}

	public void stepInto() throws IOException {
		synchronized (fLock) {
			fCli.stepInto();
		}
	}

	public void stepOver() throws IOException {
		synchronized (fLock) {
			fCli.stepOver();
		}
	}

	public void stepReturn() throws IOException {
		synchronized (fLock) {
			fCli.stepReturn();
		}
	}

	public void terminate() throws DebugException, IOException {
		synchronized (fLock) {
			fCli.terminate();
		}
	}

}
