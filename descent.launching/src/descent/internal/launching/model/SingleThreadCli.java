package descent.internal.launching.model;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.eclipse.core.resources.IResource;
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

public class SingleThreadCli implements ICli {
	
	private final ICli fCli;
	private ReentrantLock fReadOutLock;
	private ReentrantLock fReadErrorLock;
	private ReentrantLock fWriteLock;

	public SingleThreadCli(ICli cli) {
		this.fCli = cli;
		this.fReadOutLock = new ReentrantLock(true);
		this.fReadErrorLock = new ReentrantLock(true);
		this.fWriteLock = new ReentrantLock(true);
	}
	
	public boolean isSingleThread() {
		return true;
	}
	
	public String getEndCommunicationString() {
		return fCli.getEndCommunicationString();
	}
	
	public List<String> getDebugeeCommandLineArguments(String[] arguments) {
		return fCli.getDebugeeCommandLineArguments(arguments);
	}
	
	public List<String> getDebuggerCommandLineArguments() {
		return fCli.getDebuggerCommandLineArguments();
	}

	public void addBreakpoint(IResource resource, int lineNumber) throws DebugException, IOException {
		fWriteLock.lock();
		try {
			fCli.addBreakpoint(resource, lineNumber);
		} finally {
			fWriteLock.unlock();
		}
	}

	public IDescentVariable evaluateExpression(int stackFrameNumber, String expression) throws IOException {
		fWriteLock.lock();
		try {
			return fCli.evaluateExpression(stackFrameNumber, expression);
		} finally {
			fWriteLock.unlock();
		}
	}

	public IRegister[] getRegisters(IRegisterGroup registerGroup) throws IOException {
		fWriteLock.lock();
		try {
			return fCli.getRegisters(registerGroup);
		} finally {
			fWriteLock.unlock();
		}
	}

	public IStackFrame[] getStackFrames() throws DebugException, IOException {
		fWriteLock.lock();
		try {
			return fCli.getStackFrames();
		} finally {
			fWriteLock.unlock();
		}
	}

	public IVariable[] getVariables(int stackFrameNumber) throws IOException {
		fWriteLock.lock();
		try {
			return fCli.getVariables(stackFrameNumber);
		} finally {
			fWriteLock.unlock();
		}
	}
	
	public byte[] getMemoryBlock(long startAddress, long length) throws IOException {
		fWriteLock.lock();
		try {
			return fCli.getMemoryBlock(startAddress, length);
		} finally {
			fWriteLock.unlock();
		}
	}

	public void initialize(ICliRequestor requestor, IDescentDebugElementFactory factory, IStreamsProxy out, int timeout, boolean showBaseMembersInSameLevel) {
		fCli.initialize(requestor, factory, out, timeout, showBaseMembersInSameLevel);
	}

	public void interpret(String text) throws DebugException, IOException {
		fReadOutLock.lock();
		try {
			fCli.interpret(text);
		} finally {
			fReadOutLock.unlock();
		}
	}
	
	public void interpretError(String text) throws DebugException, IOException {
		fReadErrorLock.lock();
		try {
			fCli.interpretError(text);
		} finally {
			fReadErrorLock.unlock();
		}
	}

	public void removeBreakpoint(IResource resource, int lineNumber) throws DebugException, IOException {
		fWriteLock.lock();
		try {
			fCli.removeBreakpoint(resource, lineNumber);
		} finally {
			fWriteLock.unlock();
		}
	}

	public void resume() throws DebugException, IOException {
		fWriteLock.lock();
		try {
			fCli.resume();
		} finally {
			fWriteLock.unlock();
		}
	}

	public void setStackFrame(int stackFrameNumber) throws DebugException, IOException {
		fWriteLock.lock();
		try {
			fCli.setStackFrame(stackFrameNumber);
		} finally {
			fWriteLock.unlock();
		}
	}
	
	public void start() throws DebugException, IOException {
		fWriteLock.lock();
		try {
			fCli.start();
		} finally {
			fWriteLock.unlock();
		}
	}

	public void stepInto() throws IOException {
		fWriteLock.lock();
		try {
			fCli.stepInto();
		} finally {
			fWriteLock.unlock();
		}
	}

	public void stepOver() throws IOException {
		fWriteLock.lock();
		try {
			fCli.stepOver();
		} finally {
			fWriteLock.unlock();
		}
	}

	public void stepReturn() throws IOException {
		fWriteLock.lock();
		try {
			fCli.stepReturn();
		} finally {
			fWriteLock.unlock();
		}
	}

	public void terminate() throws DebugException, IOException {
		fCli.terminate();
	}

}
