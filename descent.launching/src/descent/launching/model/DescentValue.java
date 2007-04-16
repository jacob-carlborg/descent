package descent.launching.model;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;

public class DescentValue extends DescentDebugElement implements IValue {

	private final String name;
	private final String value;

	public DescentValue(DescentDebugTarget target, String name, String value) {
		super(target);
		this.name = name;
		this.value = value;
	}

	public String getReferenceTypeName() throws DebugException {
		return name;
	}

	public String getValueString() throws DebugException {
		return value;
	}

	public IVariable[] getVariables() throws DebugException {
		return null;
	}

	public boolean hasVariables() throws DebugException {
		return false;
	}

	public boolean isAllocated() throws DebugException {
		return true;
	}

}
