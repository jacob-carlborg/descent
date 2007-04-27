package descent.launching.model.ddbg;

import java.util.ArrayList;
import java.util.List;

public class DdbgVariable {
	
	private String fName;
	private String fValue;
	private DdbgVariable fParent;
	private List<DdbgVariable> fVariables;
	
	public DdbgVariable(String name) {
		this(name, null);
	}
	
	public DdbgVariable(String name, String value) {
		this.fName = name;
		this.fValue = value;
		fVariables = new ArrayList<DdbgVariable>();
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
	
	public DdbgVariable getParent() {
		return fParent;
	}
	
	public void addChild(DdbgVariable variable) {
		variable.fParent = this;
		fVariables.add(variable);
	}
	
	public List<DdbgVariable> getChildren() {
		return fVariables;
	}

}
