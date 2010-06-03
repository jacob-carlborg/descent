package descent.internal.core.ctfe;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IRegisterGroup;
import org.eclipse.debug.core.model.IVariable;

import descent.core.ICompilationUnit;
import descent.core.ctfe.IDebugger;
import descent.core.ctfe.IDescentStackFrame;
import descent.core.ctfe.IDescentThread;
import descent.core.ctfe.IDescentValue;
import descent.internal.compiler.parser.InterState;
import descent.internal.compiler.parser.Scope;

public class DescentStackFrame extends DescentDebugElement implements IDescentStackFrame {

	private final IDescentThread fThread;
	private final IDebugger fDebugger;
	private String fName;
	private int fNumber;
	private ICompilationUnit fUnit;
	private int fLineNumber;
	
	private IVariable[] fVariables;
	private final Scope scope;
	private final InterState is;
	
	public DescentStackFrame(IDebugTarget target, IDebugger debugger, IDescentThread thread, String name, int number, ICompilationUnit unit, int lineNumber, Scope scope, InterState is) {
		super(target);
		this.fDebugger = debugger;
		this.fThread = thread;
		this.fName = name;
		this.fNumber = number;
		this.fUnit = unit;
		this.fLineNumber = lineNumber;
		this.scope = scope;
		this.is = is;
	}
	
	public Scope getScope() {
		return scope;
	}
	
	public InterState getInterState() {
		return is;
	}
	
	public IDebugger getDebugger() {
		return fDebugger;
	}
	
	public int getNumber() {
		return fNumber;
	}
	
	public int getCharEnd() throws DebugException {
		return -1;
	}

	public int getCharStart() throws DebugException {
		return -1;
	}
	
	public void setLineNumber(int lineNumber) {
		fLineNumber = lineNumber;
	}

	public int getLineNumber() throws DebugException {
		return fLineNumber;
	}
	
	public ICompilationUnit getCompilationUnit() {
		return fUnit;
	}

	public String getName() throws DebugException {
		StringBuilder sb = new StringBuilder();
		sb.append(fName);
		if (fLineNumber != -1) {
			sb.append(": line "); //$NON-NLS-1$
			sb.append(fLineNumber);
		}
		return sb.toString();
	}

	public IRegisterGroup[] getRegisterGroups() throws DebugException {
		return new IRegisterGroup[0];
	}

	public IDescentThread getThread() {
		return fThread;
	}

	public IVariable[] getVariables() throws DebugException {
		IVariable[] newVariables = fDebugger.getVariables(fNumber);
		fVariables = mergeVariables(fVariables, newVariables);
		return fVariables;
	}

	private IVariable[] mergeVariables(IVariable[] oldVariables, IVariable[] newVariables) throws DebugException {
		if (oldVariables == null) {
			return newVariables;
		}
		
		for(int i = 0; i < oldVariables.length && i < newVariables.length; i++) {
			DescentVariable oldVar = (DescentVariable) oldVariables[i];
			DescentVariable newVar = (DescentVariable) newVariables[i];
			if (oldVar.getName().equals(newVar.getName())) {
				IDescentValue oldValue = oldVar.getValue();
				IDescentValue newValue = newVar.getValue();
				
				if (oldValue != null && newValue != null && oldValue.getValueString() != null && newValue.getValueString() != null) {				
					if (!oldValue.getValueString().equals(newValue.getValueString())) {
						newVar.setHasValueChanged(true);
					}
					if (!oldValue.isLazy() && !newValue.isLazy() && oldValue.hasVariables() &&  oldValue.hasVariables() == newValue.hasVariables()) {
						mergeVariables(oldValue.getVariables(), newValue.getVariables());
					}
				}
			}
		}
		
		return newVariables;
	}

	public boolean hasRegisterGroups() throws DebugException {
		return false;
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
		getThread().stepInto(getNumber());
	}

	public void stepOver() throws DebugException {
		getThread().stepOver(getNumber());
	}

	public void stepReturn() throws DebugException {
		getThread().stepReturn(getNumber());
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
	
	public boolean isInSameFunction(DescentStackFrame other) {
		if (fName != null && other.fName != null && fName.equals(other.fName) && fNumber == other.fNumber) {
			if (fUnit != null && other.fUnit != null && fUnit.equals(other.fUnit)) {
				return true;
			}
		}
		return false;
	}
	
	public void merge(DescentStackFrame other) {
		this.fLineNumber = other.fLineNumber;
		this.fName = other.fName;
		this.fNumber = other.fNumber;
		this.fUnit = other.fUnit;
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
