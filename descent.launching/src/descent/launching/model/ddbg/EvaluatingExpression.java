package descent.launching.model.ddbg;

import java.io.IOException;

import org.eclipse.debug.core.DebugException;

public class EvaluatingExpression implements IState {

	public DdbgVariable fVariable;
	private final String fExpression;
	private final DdbgCli fCli;
	
	public EvaluatingExpression(DdbgCli cli, String expression) {
		this.fCli = cli;
		this.fExpression = expression;
	}

	public void interpret(String text) throws DebugException, IOException {
		if (text.equals("->")) {
			fCli.notifyStateReturn();
		} else {
			parseVariable(text);
		}
	}

	private void parseVariable(String text) {
		if ("{".equals(text.trim())) {
			fVariable = new DdbgVariable(fExpression);
			return;
		}
		
		if ("}".equals(text.trim())) {
			if (fVariable.getParent() != null) {
				fVariable = fVariable.getParent();
			}
			return;
		}
		
		int indexOfEquals = text.indexOf('=');
		if (indexOfEquals == -1) {
			fVariable = new DdbgVariable(fExpression, text);
			return;
		}
		
		String name = text.substring(0, indexOfEquals).trim();
		String value = text.substring(indexOfEquals + 1).trim();
		if ("{".equals(value.trim())) {
			DdbgVariable newVariable = new DdbgVariable(name, null);
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
			
			DdbgVariable newVariable = new DdbgVariable(name, value);
			if (fVariable == null) {
				fVariable = newVariable;
			} else {
				fVariable.addChild(newVariable);
			}
		}
	}
	
}
