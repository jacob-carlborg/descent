package descent.internal.core.ctfe;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;

public class DescentCtfeVariable extends DescentCtfeDebugElement implements ICtfeParentVariable {

	private final String fName;
	private final DescentCtfeValue fValue;
	private boolean fHasValueChanged;
	
	public DescentCtfeVariable(IDebugTarget target, IDebugger debugger, int stackFrame, String name, String value) {
		this(target, debugger, stackFrame, name, value, null);
	}

	public DescentCtfeVariable(IDebugTarget target, IDebugger debugger, int stackFrame, String name, String value, String expression) {
		super(target);
		this.fName = name;
		this.fValue = new DescentCtfeValue(target, debugger, stackFrame, name, value, expression);
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
	
	public void addChild(IVariable variable) {
		fValue.addVariable(variable);
	}
	
	public void addChildren(IVariable[] variables) {
		for(IVariable variable : variables) {
			addChild(variable);
		}
	}
	
	public boolean isLazy() {
		return fValue.isLazy();
	}

}
