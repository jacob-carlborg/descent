package descent.internal.core.ctfe;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IValue;

import descent.internal.compiler.parser.Expression;

public class DescentCtfeVariable extends DescentCtfeDebugElement implements IDescentCtfeVariable {

	private final String fName;
	private final DescentCtfeValue fValue;
	private boolean fHasValueChanged;
	
	public DescentCtfeVariable(IDebugTarget target, CtfeDebugger debugger, int stackFrame, String name, Expression value) {
		super(target);
		this.fName = name;
		this.fValue = new DescentCtfeValue(target, debugger, stackFrame, name, value);
	}

	public String getName() throws DebugException {
		return fName;
	}

	public String getReferenceTypeName() throws DebugException {
		return fName;
	}

	public DescentCtfeValue getValue() throws DebugException {
		return fValue;
	}
	
	public void setHasValueChanged(boolean hasValueChanged) {
		fHasValueChanged = hasValueChanged;
	}

	public boolean hasValueChanged() throws DebugException {
		return fHasValueChanged;
	}

	public void setValue(String expression) throws DebugException {
	}

	public void setValue(IValue value) throws DebugException {
	}

	public boolean supportsValueModification() {
		return false;
	}

	public boolean verifyValue(String expression) throws DebugException {
		return false;
	}

	public boolean verifyValue(IValue value) throws DebugException {
		return false;
	}
	
	public boolean isLazy() {
		return fValue.isLazy();
	}

}
