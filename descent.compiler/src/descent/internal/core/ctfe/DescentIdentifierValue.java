package descent.internal.core.ctfe;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IVariable;

import descent.core.ctfe.IDescentValue;

public class DescentIdentifierValue extends DescentDebugElement implements IDescentValue {

	private final String fName;
	private final String fIdentifier;

	public DescentIdentifierValue(IDebugTarget target, String name, String value) {
		super(target);
		this.fName = name;
		this.fIdentifier = value;
	}

	public String getReferenceTypeName() throws DebugException {
		return fName;
	}

	public String getValueString() throws DebugException {
		return fIdentifier.toString();
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
		return fIdentifier != null;
	}
	
	public String getDetail() throws DebugException {
		return getValueString();
	}

}
