package descent.launching.model.gdb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.debug.core.DebugException;

public class ConsultingVariables implements IState {
	
	public List<GdbVariable> fVariables = new ArrayList<GdbVariable>();
	private GdbVariable fVariable;
	private final GdbDebugger fCli;
	
	public ConsultingVariables(GdbDebugger cli) {
		this.fCli = cli;
	}

	public void interpret(String text) throws DebugException, IOException {
		if (text.equals("(gdb) ")) {
			fCli.notifyStateReturn();
		} else {
			parseVariable(text);
		}
	}
	
	public void interpretError(String text) throws DebugException, IOException {
		// Nothing to do
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
		
		boolean nameIsBase = name.indexOf('<') != -1;
		
		if (value.length() > 0 && value.charAt(value.length() - 1) == '{') {
			GdbVariable newVariable = new GdbVariable(name);
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
			
			GdbVariable newVariable;
			
			if (value.length() > 0 && value.charAt(0) == '@') {
				newVariable = new GdbVariable(name);
				newVariable.setLazy(true);
				newVariable.setIsBase(nameIsBase);
			} else {
				newVariable= new GdbVariable(name, value);
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