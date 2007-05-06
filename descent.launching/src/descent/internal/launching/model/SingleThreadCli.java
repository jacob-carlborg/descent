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

import descent.launching.model.IDebugger;
import descent.launching.model.IDebuggerListener;
import descent.launching.model.IDebugElementFactory;
import descent.launching.model.IParentVariable;

public class SingleThreadCli implements IDebugger {
	
	private final IDebugger fDebugger;
	private ReentrantLock fReadOutLock;
	private ReentrantLock fReadErrorLock;
	private ReentrantLock fWriteLock;

	public SingleThreadCli(IDebugger debugger) {
		this.fDebugger = debugger;
		this.fReadOutLock = new ReentrantLock(true);
		this.fReadErrorLock = new ReentrantLock(true);
		this.fWriteLock = new ReentrantLock(true);
	}
	
	public boolean isSingleThread() {
		return true;
	}
	
	public String getEndCommunicationString() {
		return fDebugger.getEndCommunicationString();
	}
	
	public List<String> getDebugeeCommandLineArguments(String[] arguments) {
		return fDebugger.getDebugeeCommandLineArguments(arguments);
	}
	
	public List<String> getDebuggerCommandLineArguments() {
		return fDebugger.getDebuggerCommandLineArguments();
	}

	public void addBreakpoint(IResource resource, int lineNumber) throws DebugException, IOException {
		fWriteLock.lock();
		try {
			fDebugger.addBreakpoint(resource, lineNumber);
		} finally {
			fWriteLock.unlock();
		}
	}

	public IVariable evaluateExpression(int stackFrameNumber, String expression) throws IOException {
		fWriteLock.lock();
		try {
			return fDebugger.evaluateExpression(stackFrameNumber, expression);
		} finally {
			fWriteLock.unlock();
		}
	}

	public IRegister[] getRegisters(IRegisterGroup registerGroup) throws IOException {
		fWriteLock.lock();
		try {
			return fDebugger.getRegisters(registerGroup);
		} finally {
			fWriteLock.unlock();
		}
	}

	public IStackFrame[] getStackFrames() throws DebugException, IOException {
		fWriteLock.lock();
		try {
			return fDebugger.getStackFrames();
		} finally {
			fWriteLock.unlock();
		}
	}

	public IVariable[] getVariables(int stackFrameNumber) throws IOException {
		fWriteLock.lock();
		try {
			return fDebugger.getVariables(stackFrameNumber);
		} finally {
			fWriteLock.unlock();
		}
	}
	
	public byte[] getMemoryBlock(long startAddress, long length) throws IOException {
		fWriteLock.lock();
		try {
			return fDebugger.getMemoryBlock(startAddress, length);
		} finally {
			fWriteLock.unlock();
		}
	}

	public void initialize(IDebuggerListener listener, IDebugElementFactory factory, IStreamsProxy out, int timeout, boolean showBaseMembersInSameLevel) {
		fDebugger.initialize(listener, factory, out, timeout, showBaseMembersInSameLevel);
	}

	public void interpret(String text) throws DebugException, IOException {
		fReadOutLock.lock();
		try {
			fDebugger.interpret(text);
		} finally {
			fReadOutLock.unlock();
		}
	}
	
	public void interpretError(String text) throws DebugException, IOException {
		fReadErrorLock.lock();
		try {
			fDebugger.interpretError(text);
		} finally {
			fReadErrorLock.unlock();
		}
	}

	public void removeBreakpoint(IResource resource, int lineNumber) throws DebugException, IOException {
		fWriteLock.lock();
		try {
			fDebugger.removeBreakpoint(resource, lineNumber);
		} finally {
			fWriteLock.unlock();
		}
	}

	public void resume() throws DebugException, IOException {
		fWriteLock.lock();
		try {
			fDebugger.resume();
		} finally {
			fWriteLock.unlock();
		}
	}
	
	public void start() throws DebugException, IOException {
		fWriteLock.lock();
		try {
			fDebugger.start();
		} finally {
			fWriteLock.unlock();
		}
	}

	public void stepInto() throws IOException {
		fWriteLock.lock();
		try {
			fDebugger.stepInto();
		} finally {
			fWriteLock.unlock();
		}
	}

	public void stepOver() throws IOException {
		fWriteLock.lock();
		try {
			fDebugger.stepOver();
		} finally {
			fWriteLock.unlock();
		}
	}

	public void stepReturn() throws IOException {
		fWriteLock.lock();
		try {
			fDebugger.stepReturn();
		} finally {
			fWriteLock.unlock();
		}
	}

	public void terminate() throws DebugException, IOException {
		fDebugger.terminate();
	}

}