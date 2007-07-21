package descent.internal.debug.core.model;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;

import descent.debug.core.model.IDebugger;
import descent.debug.core.model.IParentVariable;

public class DescentVariable extends DescentDebugElement implements IParentVariable {

	private final String fName;
	private final DescentValue fValue;
	private boolean fHasValueChanged;
	
	public DescentVariable(IDebugTarget target, IDebugger debugger, int stackFrame, String name, String value) {
		this(target, debugger, stackFrame, name, value, null);
	}

	public DescentVariable(IDebugTarget target, IDebugger debugger, int stackFrame, String name, String value, String expression) {
		super(target);
		this.fName = name;
		this.fValue = new DescentValue(target, debugger, stackFrame, name, value, expression);
	}

	public String getName() throws DebugException {
		return fName;
	}

	public String getReferenceTypeName() throws DebugException {
		return fName;
	}

	public DescentValue getValue() throws DebugException {
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
