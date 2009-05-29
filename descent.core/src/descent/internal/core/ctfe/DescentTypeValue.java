package descent.internal.core.ctfe;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IVariable;

import descent.core.ctfe.IDescentValue;
import descent.internal.compiler.parser.Type;

public class DescentTypeValue extends DescentDebugElement implements IDescentValue {

	private final String fName;
	private final Type fType;

	public DescentTypeValue(IDebugTarget target, String name, Type value) {
		super(target);
		this.fName = name;
		this.fType = value;
	}

	public String getReferenceTypeName() throws DebugException {
		return fName;
	}

	public String getValueString() throws DebugException {
		return fType.toString();
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
	
	public boolean isLazy() {
		return fType != null;
	}
	
	public String getDetail() throws DebugException {
		return getValueString();
	}

}
