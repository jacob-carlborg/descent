package descent.internal.launching.model;

import java.io.IOException;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IRegisterGroup;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IVariable;

import descent.launching.model.ICli;


public class DescentStackFrame extends DescentDebugElement implements IStackFrame {

	private final IThread fThread;
	private final ICli fCli;
	private String fName;
	private int fNumber;
	private String fSourceName;
	private int fLineNumber;
	
	public DescentStackFrame(IDebugTarget target, ICli cli, IThread thread, String name, int number, String sourceName, int lineNumber) {
		super(target);
		this.fCli = cli;
		this.fThread = thread;
		this.fName = name;
		this.fNumber = number;
		this.fSourceName = sourceName;
		this.fLineNumber = lineNumber;
	}
	
	public ICli getCli() {
		return fCli;
	}
	
	public int getNumber() {
		return fNumber;
	}
	
	public int getCharEnd() throws DebugException {
		return 0;
	}

	public int getCharStart() throws DebugException {
		return 0;
	}

	public int getLineNumber() throws DebugException {
		return fLineNumber;
	}
	
	public String getSourceName() {
		return fSourceName;
	}

	public String getName() throws DebugException {
		StringBuilder sb = new StringBuilder();
		sb.append(fName);
		if (fLineNumber != -1) {
			sb.append(": line ");
			sb.append(fLineNumber);
		}
		return sb.toString();
	}

	public IRegisterGroup[] getRegisterGroups() throws DebugException {
		return new IRegisterGroup[] {
				new DescentRegisterGroup((DescentDebugTarget) getDebugTarget(), fCli, fNumber)
		};
	}

	public IThread getThread() {
		return fThread;
	}

	public IVariable[] getVariables() throws DebugException {
		try {
			return fCli.getVariables(fNumber);
		} catch (IOException e) {
			e.printStackTrace();
			return new IVariable[0];
		}
	}

	public boolean hasRegisterGroups() throws DebugException {
		return true;
	}

	public boolean hasVariables() throws DebugException {
		return true;
	}

	public boolean canStepInto() {
		return getThread().canStepInto();
	}

	public boolean canStepOver() {
		return getThread().canStepOver();
	}

	public boolean canStepReturn() {
		return getThread().canStepReturn();
	}

	public boolean isStepping() {
		return getThread().isStepping();
	}

	public void stepInto() throws DebugException {
		getThread().stepInto();
	}

	public void stepOver() throws DebugException {
		getThread().stepOver();
	}

	public void stepReturn() throws DebugException {
		getThread().stepReturn();
	}

	public boolean canResume() {
		return getThread().canResume();
	}

	public boolean canSuspend() {
		return getThread().canSuspend();
	}

	public boolean isSuspended() {
		return getThread().isSuspended();
	}

	public void resume() throws DebugException {
		getThread().resume();
	}

	public void suspend() throws DebugException {
		getThread().suspend();
	}

	public boolean canTerminate() {
		return getThread().canTerminate();
	}

	public boolean isTerminated() {
		return getThread().isTerminated();
	}

	public void terminate() throws DebugException {
		getThread().terminate();
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
