package descent.internal.launching.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;

import descent.launching.model.IDebugger;
import descent.launching.model.IParentVariable;

public class DescentValue extends DescentDebugElement implements IValue {
	
	private final static IVariable[] NO_VARIABLES = new IVariable[0];

	private final String fName;
	protected String fValue;
	private final String fExpression;
	private final IDebugger fDebugger;
	private List<IVariable> fVariables;
	private final int fStackFrame;	
	
	public DescentValue(IDebugTarget target, IDebugger debugger, int stackFrame, String name, String value) {
		this(target, debugger, stackFrame, name, value, null);
	}

	public DescentValue(IDebugTarget target, IDebugger debugger, int stackFrame, String name, String value, String expression) {
		super(target);
		this.fDebugger = debugger;
		this.fStackFrame = stackFrame;
		this.fName = name;
		this.fValue = value;
		this.fExpression = expression;
	}

	public String getReferenceTypeName() throws DebugException {
		return fName;
	}

	public String getValueString() throws DebugException {
		return fValue;
	}

	public IVariable[] getVariables() throws DebugException {
		if (fVariables != null) {
			return fVariables.toArray(new IVariable[fVariables.size()]);
		}
		
		if (fExpression != null) {
			try {
				IVariable variable = fDebugger.evaluateExpression(fStackFrame, fExpression);
				if (variable == null) {
					return NO_VARIABLES;
				}
				
				if (variable.getValue().hasVariables()) {
					IVariable[] subVariables = variable.getValue().getVariables();
					fVariables = new ArrayList<IVariable>(subVariables.length);
					for(IVariable var : subVariables) {
						fVariables.add((IParentVariable) var);
					}
					return subVariables;
				} else {
					return new IVariable[] { variable };
				}
			} catch (IOException e) {
				e.printStackTrace();
				return NO_VARIABLES;
			}
		}
		
		return null;
	}

	public boolean hasVariables() throws DebugException {
		return fVariables != null || fExpression != null;
	}

	public boolean isAllocated() throws DebugException {
		return true;
	}
	
	public void addVariable(IVariable variable) {
		if (this.fVariables == null) {
			this.fVariables = new ArrayList<IVariable>();
		}
		this.fVariables.add(variable);
	}
	
	public boolean isLazy() {
		return fExpression != null;
	}

}
