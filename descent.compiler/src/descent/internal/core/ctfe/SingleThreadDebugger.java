package descent.internal.core.ctfe;

import java.util.concurrent.locks.ReentrantLock;

import org.eclipse.core.resources.IResource;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IVariable;

import descent.core.ctfe.IDebugElementFactory;
import descent.core.ctfe.IDebugger;
import descent.core.ctfe.IDebuggerListener;
import descent.internal.compiler.parser.ASTDmdNode;
import descent.internal.compiler.parser.InterState;
import descent.internal.compiler.parser.Scope;

public class SingleThreadDebugger implements IDebugger {
	
	private final IDebugger fDebugger;
	private ReentrantLock fLock;
	
	public SingleThreadDebugger(IDebugger debugger) {
		this.fDebugger = debugger;
		this.fLock = new ReentrantLock(true);
	}
	
	public void initialize(IDebuggerListener listener, IDebugElementFactory elementFactory) {
		fDebugger.initialize(listener, elementFactory);
	}

	public void addBreakpoint(IResource resource, int lineNumber) {
		fLock.lock();
		try {
			fDebugger.addBreakpoint(resource, lineNumber);
		} finally {
			fLock.unlock();
		}
	}

	public void enterStackFrame(ASTDmdNode node) {
		fLock.lock();
		try {
			fDebugger.enterStackFrame(node);
		} finally {
			fLock.unlock();
		}
	}

	public IVariable evaluateExpression(int stackFrame, String expression) {
		fLock.lock();
		try {
			return fDebugger.evaluateExpression(stackFrame, expression);
		} finally {
			fLock.unlock();
		}
	}

	public void exitStackFrame(ASTDmdNode node) {
		fLock.lock();
		try {
			fDebugger.exitStackFrame(node);
		} finally {
			fLock.unlock();
		}
	}

	public IStackFrame[] getStackFrames() {
		fLock.lock();
		try {
			return fDebugger.getStackFrames();
		} finally {
			fLock.unlock();
		}
	}

	public IVariable[] getVariables(int stackFrame) {
		fLock.lock();
		try {
			return fDebugger.getVariables(stackFrame);
		} finally {
			fLock.unlock();
		}
	}

	public void message(String message) {
		fLock.lock();
		try {
			fDebugger.message(message);
		} finally {
			fLock.unlock();
		}
	}

	public void removeBreakpoint(IResource resource, int lineNumber) {
		fLock.lock();
		try {
			fDebugger.removeBreakpoint(resource, lineNumber);
		} finally {
			fLock.unlock();
		}
	}

	public void resume() {
		fLock.lock();
		try {
			fDebugger.resume();
		} finally {
			fLock.unlock();
		}
	}

	public void start() {
		fLock.lock();
		try {
			fDebugger.start();
		} finally {
			fLock.unlock();
		}
	}

	public void stepBegin(ASTDmdNode node, Scope sc) {
		fLock.lock();
		try {
			fDebugger.stepBegin(node, sc);
		} finally {
			fLock.unlock();
		}
	}

	public void stepBegin(ASTDmdNode node, InterState is) {
		fLock.lock();
		try {
			fDebugger.stepBegin(node, is);
		} finally {
			fLock.unlock();
		}
	}

	public void stepEnd(ASTDmdNode node, Scope sc) {
		fLock.lock();
		try {
			fDebugger.stepEnd(node, sc);
		} finally {
			fLock.unlock();
		}
	}

	public void stepEnd(ASTDmdNode node, InterState is) {
		fLock.lock();
		try {
			fDebugger.stepEnd(node, is);
		} finally {
			fLock.unlock();
		}
	}

	public void stepInto() {
		fLock.lock();
		try {
			fDebugger.stepInto();
		} finally {
			fLock.unlock();
		}
	}
	
	public void stepInto(int stackFrame) {
		fLock.lock();
		try {
			fDebugger.stepInto(stackFrame);
		} finally {
			fLock.unlock();
		}
	}

	public void stepOver() {
		fLock.lock();
		try {
			fDebugger.stepOver();
		} finally {
			fLock.unlock();
		}
	}
	
	public void stepOver(int stackFrame) {
		fLock.lock();
		try {
			fDebugger.stepOver(stackFrame);
		} finally {
			fLock.unlock();
		}
	}

	public void stepReturn() {
		fLock.lock();
		try {
			fDebugger.stepReturn();
		} finally {
			fLock.unlock();
		}
	}
	
	public void stepReturn(int stackFrame) {
		fLock.lock();
		try {
			fDebugger.stepReturn(stackFrame);
		} finally {
			fLock.unlock();
		}
	}

	public void terminate() {
		fLock.lock();
		try {
			fDebugger.terminate();
		} finally {
			fLock.unlock();
		}
	}

}
