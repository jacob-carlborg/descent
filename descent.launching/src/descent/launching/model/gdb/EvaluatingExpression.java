package descent.launching.model.gdb;

import java.io.IOException;

import org.eclipse.debug.core.DebugException;

public class EvaluatingExpression implements IState {

	public GdbVariable fVariable;
	private final String fExpression;
	private final GdbDebugger fCli;
	
	public EvaluatingExpression(GdbDebugger cli, String expression) {
		this.fCli = cli;
		this.fExpression = expression;
	}

	public void interpret(String text) throws DebugException, IOException {
		if (text.equals("(gdb) ")) {
			fCli.notifyStateReturn();
		} else {
			parseVariable(text);
		}
	}
	
	public void interpretError(String text) throws DebugException, IOException {
		fCli.notifyStateReturn();
	}

	private void parseVariable(String text) {
		text = text.trim();
		
		if (text.startsWith("members of ")) {
			return;
		}
		
		if ("{".equals(text)) {
			fVariable = new GdbVariable(fExpression);
			return;
		}
		
		if ("}".equals(text) || "},".equals(text)) {
			if (fVariable.getParent() != null) {
				fVariable = fVariable.getParent();
			}
			return;
		}
		
		int indexOfEquals = text.indexOf('=');
		if (indexOfEquals == -1) {
			fVariable = new GdbVariable(fExpression, text);
			return;
		}
		
		String name = text.substring(0, indexOfEquals).trim();
		String value = text.substring(indexOfEquals + 1).trim();
		
		boolean nameIsBase = name.indexOf('<') != -1;
		
		if (value.length() > 0 && value.charAt(value.length() - 1) == '{') {
			GdbVariable newVariable = new GdbVariable(name, null);
			newVariable.setIsBase(nameIsBase);
			if (fVariable != null) {
				fVariable.addChild(newVariable);
			}
			fVariable = newVariable;
		} else {
			if (fVariable != null) {
				value = value.trim();
				if (value.length() > 0 && value.charAt(value.length() - 1) == ',') {
					value = value.substring(0, value.length() - 1);; 
				}
			}
			
			GdbVariable newVariable;
			
			if (value.length() > 0 && value.charAt(0) == '@') {
				newVariable = new GdbVariable(name);
				newVariable.setLazy(true);
				newVariable.setIsBase(nameIsBase);
			} else {
				newVariable = new GdbVariable(name, value);
				newVariable.setIsBase(nameIsBase);
			}
			if (fVariable == null) {
				fVariable = newVariable;
			} else {
				fVariable.addChild(newVariable);
			}
		}
	}
	
}
