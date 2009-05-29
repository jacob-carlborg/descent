package descent.internal.core.ctfe;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IValue;

import descent.core.ctfe.IDebugElementFactory;
import descent.core.ctfe.IDescentValue;
import descent.core.ctfe.IDescentVariable;
import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.TupleDeclaration;
import descent.internal.compiler.parser.Type;

public class DescentVariable extends DescentDebugElement implements IDescentVariable {

	private final String fName;
	private final IDescentValue fValue;
	private boolean fHasValueChanged;
	
	public DescentVariable(IDebugTarget target, IDebugElementFactory elementFactory, int stackFrame, String name, Expression value) {
		super(target);
		this.fName = name;
		this.fValue = new DescentExpressionValue(target, elementFactory, stackFrame, name, value);
	}
	
	public DescentVariable(IDebugTarget target, IDebugElementFactory elementFactory, int stackFrame, String name, Type value) {
		super(target);
		this.fName = name;
		this.fValue = new DescentTypeValue(target, name, value);
	}
	
	public DescentVariable(IDebugTarget target, IDebugElementFactory elementFactory, int stackFrame, String name, String value) {
		super(target);
		this.fName = name;
		this.fValue = new DescentIdentifierValue(target, name, value);
	}
	
	public DescentVariable(IDebugTarget target, IDebugElementFactory elementFactory, int stackFrame, String name, TupleDeclaration value) {
		super(target);
		this.fName = name;
		this.fValue = new DescentTupleValue(target, elementFactory, stackFrame, name, value);
	}

	public String getName() throws DebugException {
		return fName;
	}

	public String getReferenceTypeName() throws DebugException {
		return fName;
	}

	public IDescentValue getValue() throws DebugException {
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
