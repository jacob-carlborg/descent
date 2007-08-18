package descent.internal.debug.core.model.gdb;

import java.util.ArrayList;
import java.util.List;

public class GdbVariable {
	
	private String fName;
	private String fValue;
	private GdbVariable fParent;
	private List<GdbVariable> fVariables;
	private boolean fLazy;
	private boolean fIsBase;
	
	public GdbVariable(String name) {
		this(name, null);
	}
	
	public GdbVariable(String name, String value) {
		this.fName = name;
		this.fValue = value;
		fVariables = new ArrayList<GdbVariable>();
	}
	
	public void setName(String name) {
		fName = name;
	}
	
	public void setValue(String value) {
		fValue = value;
	}
	
	public String getName() {
		return fName;
	}
	
	public String getValue() {
		return fValue;
	}
	
	public GdbVariable getParent() {
		return fParent;
	}
	
	public void addChild(GdbVariable variable) {
		variable.fParent = this;
		fVariables.add(variable);
	}
	
	public List<GdbVariable> getChildren() {
		return fVariables;
	}
	
	public void setLazy(boolean lazy) {
		fLazy = lazy;
	}
	
	public boolean isLazy() {
		return fLazy;
	}
	
	public void setIsBase(boolean isBase) {
		fIsBase = isBase;
	}
	
	public boolean isBase() {
		return fIsBase;
	}
	
	public String getExpression() {
		if (fIsBase) {
			return fParent.getExpression();
		}
		
		if (fParent == null) {
			return fName;
		} else {
			return fParent.getExpression() + "." + fName; //$NON-NLS-1$
		}
	}	

}
