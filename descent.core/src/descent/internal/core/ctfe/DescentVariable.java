package descent.internal.core.ctfe;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IValue;

import descent.core.ctfe.IDebugElementFactory;
import descent.core.ctfe.IDescentVariable;
import descent.internal.compiler.parser.Expression;

public class DescentVariable extends DescentDebugElement implements IDescentVariable {

	private final String fName;
	private final DescentValue fValue;
	private boolean fHasValueChanged;
	
	public DescentVariable(IDebugTarget target, IDebugElementFactory elementFactory, int stackFrame, String name, Expression value) {
		super(target);
		this.fName = name;
		this.fValue = new DescentValue(target, elementFactory, stackFrame, name, value);
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
	
	public boolean isLazy() {
		return fValue.isLazy();
	}

}
