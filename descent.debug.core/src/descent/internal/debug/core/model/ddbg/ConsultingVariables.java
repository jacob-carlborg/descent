package descent.internal.debug.core.model.ddbg;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.debug.core.DebugException;

public class ConsultingVariables implements IState {
	
	public List<DdbgVariable> fVariables = new ArrayList<DdbgVariable>();
	private DdbgVariable fVariable;
	private final DdbgDebugger fCli;
	
	public ConsultingVariables(DdbgDebugger cli) {
		this.fCli = cli;
	}

	public void interpret(String text) throws DebugException, IOException {
		if (text.equals("->")) {
			fCli.notifyStateReturn();
		} else {
			parseVariable(text);
		}
	}
	
	/*
	 * Variables come in two flavors:
	 * var = value
	 * var = {
	 *    subValue1 = ...,
	 *    subValue2 = ...,
	 *    (recursively)
	 * }
	 */
	private void parseVariable(String text) {
		text = text.trim();
		
		if ("}".equals(text) || "},".equals(text)) {
			fVariable = fVariable.getParent();
			return;
		}
		
		int indexOfEquals = text.indexOf('=');
		if (indexOfEquals == -1) return;
		
		String name = text.substring(0, indexOfEquals).trim();
		String value = text.substring(indexOfEquals + 1).trim();
		
		boolean nameIsBase = name.indexOf('.') != -1;
		
		if ("{".equals(value)) {
			DdbgVariable newVariable = new DdbgVariable(name);
			newVariable.setIsBase(nameIsBase);
			if (fVariable == null) {
				fVariables.add(newVariable);
				fVariable = newVariable;
			} else {
				fVariable.addChild(newVariable);
				fVariable = newVariable;
			}
		} else {
			if (fVariable != null) {
				value = value.trim();
				if (value.length() > 0 && value.charAt(value.length() - 1) == ',') {
					value = value.substring(0, value.length() - 1);
				}
			}
			
			DdbgVariable newVariable;
			
			if ("...".equals(value)) {
				newVariable = new DdbgVariable(name);
				newVariable.setLazy(true);
				newVariable.setIsBase(nameIsBase);
			} else {
				newVariable= new DdbgVariable(name, value);
				newVariable.setIsBase(nameIsBase);
			}
			if (fVariable == null) {
				fVariables.add(newVariable);
			} else {
				fVariable.addChild(newVariable);
			}
		}
	}
	
	@Override
	public String toString() {
		return "consulting variables";
	}
	
}