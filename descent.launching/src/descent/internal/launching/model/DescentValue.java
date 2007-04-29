package descent.internal.launching.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;

import descent.launching.model.ICli;
import descent.launching.model.IDescentVariable;

public class DescentValue extends DescentDebugElement implements IValue {

	private final String fName;
	private final String fValue;
	private final String fExpression;
	private final ICli fCli;
	private List<IDescentVariable> fVariables;
	private final int fStackFrame;	
	
	public DescentValue(IDebugTarget target, ICli cli, int stackFrame, String name, String value) {
		this(target, cli, stackFrame, name, value, null);
	}

	public DescentValue(IDebugTarget target, ICli cli, int stackFrame, String name, String value, String expression) {
		super(target);
		this.fCli = cli;
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
				IDescentVariable variable = fCli.evaluateExpression(fStackFrame, fExpression);
				if (variable.getValue().hasVariables()) {
					IVariable[] subVariables = variable.getValue().getVariables();
					fVariables = new ArrayList<IDescentVariable>(subVariables.length);
					for(IVariable var : subVariables) {
						fVariables.add((IDescentVariable) var);
					}
					return subVariables;
				} else {
					return new IVariable[0];
				}
			} catch (IOException e) {
				e.printStackTrace();
				return new IVariable[0];
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
	
	public void addVariable(IDescentVariable variable) {
		if (this.fVariables == null) {
			this.fVariables = new ArrayList<IDescentVariable>();
		}
		this.fVariables.add(variable);
	}

}
