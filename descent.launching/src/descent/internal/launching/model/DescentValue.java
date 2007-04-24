package descent.internal.launching.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;

import descent.launching.model.IDescentVariable;

public class DescentValue extends DescentDebugElement implements IValue {

	private final String name;
	private final String value;
	private List<IDescentVariable> variables;

	public DescentValue(IDebugTarget target, String name, String value) {
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
		return variables.toArray(new IVariable[variables.size()]);
	}

	public boolean hasVariables() throws DebugException {
		return variables != null;
	}

	public boolean isAllocated() throws DebugException {
		return true;
	}
	
	public void addVariable(IDescentVariable variable) {
		if (this.variables == null) {
			this.variables = new ArrayList<IDescentVariable>();
		}
		this.variables.add(variable);
	}

}
