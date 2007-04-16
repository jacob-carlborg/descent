package descent.launching.model;

import java.io.IOException;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IRegisterGroup;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IVariable;

import descent.launching.model.ddbg.DdbgInterpreter;


public class DescentStackFrame extends DescentDebugElement implements IStackFrame {

	private final DescentThread fThread;
	private final DdbgInterpreter fInterpreter;
	private String fName;
	private int fNumber;	

	public DescentStackFrame(DdbgInterpreter interpreter, DescentThread thread, String data) {
		super((DescentDebugTarget) thread.getDebugTarget());
		this.fInterpreter = interpreter;
		this.fThread = thread;
		init(data);
	}
	
	private void init(String data) {
		if (data.length() > 0 && data.charAt(0) == '#') {
			int indexOfFirstSpace = data.indexOf(' ');
			
			this.fNumber = Integer.parseInt(data.substring(1, indexOfFirstSpace));			
			
			int indexOfIn = data.indexOf(" in ");
			int indexOfFrom = data.indexOf(" from ");
			int indexOfAt = data.indexOf(" at ");
			if (indexOfIn != -1 && indexOfFrom != -1 && indexOfIn < indexOfFrom) {
				fName = data.substring(indexOfIn + 4, indexOfFrom + 1);
			} else if (indexOfIn != -1 && indexOfAt != -1 && indexOfIn < indexOfAt) {
				fName = data.substring(indexOfIn + 4, indexOfAt + 1);
			} else {
				if (indexOfFirstSpace != -1) {
					if (indexOfAt != -1) {
						fName = data.substring(indexOfFirstSpace + 1, indexOfAt + 1);
					} else {
						int indexOfSecondSpace = data.indexOf(' ', indexOfFirstSpace + 1);
						if (indexOfSecondSpace != -1) {
							fName = data.substring(indexOfFirstSpace + 1, indexOfSecondSpace);
						}
					}
				}
			}
			return;
		}
		
		fName = data;
	}

	public int getCharEnd() throws DebugException {
		return 0;
	}

	public int getCharStart() throws DebugException {
		return 0;
	}

	public int getLineNumber() throws DebugException {
		return 0;
	}

	public String getName() throws DebugException {
		return fName;
	}

	public IRegisterGroup[] getRegisterGroups() throws DebugException {
		return new IRegisterGroup[] {
				new DescentRegisterGroup((DescentDebugTarget) getDebugTarget(), fInterpreter, fNumber)
		};
	}

	public IThread getThread() {
		return fThread;
	}

	public IVariable[] getVariables() throws DebugException {
		try {
			return fInterpreter.getVariables(fNumber);
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

}
