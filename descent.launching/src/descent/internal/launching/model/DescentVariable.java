package descent.internal.launching.model;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IValue;

import descent.launching.model.IDescentVariable;

public class DescentVariable extends DescentDebugElement implements IDescentVariable {

	private final String fName;
	private DescentValue fValue;
	private DescentVariable fParent;
	private boolean fHasValueChanged;

	public DescentVariable(IDebugTarget target, String name, String value) {
		super(target);
		this.fName = name;
		this.fValue = new DescentValue(target, name, value);
	}

	public String getName() throws DebugException {
		return fName;
	}

	public String getReferenceTypeName() throws DebugException {
		return fName;
	}

	public IValue getValue() throws DebugException {
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
	
	public void addChild(IDescentVariable variable) {
		((DescentVariable) variable).fParent = this;
		fValue.addVariable(variable);
	}
	
	public void addChildren(IDescentVariable[] variables) {
		for(IDescentVariable variable : variables) {
			addChild(variable);
		}
	}
	
	public IDescentVariable getParent() {
		return fParent;
	}
	
	/*
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof DescentVariable)) {
			return false;
		}
		
		DescentVariable other = (DescentVariable) obj;
		if (fName.equals(other.fName)) {
			if (fParent == null && other.fParent == null) {
				return true;
			} else if (fParent != null && other.fParent != null) {
				return fParent.equals(other.fParent);
			}
		}
		return false;
	}
	*/

}
