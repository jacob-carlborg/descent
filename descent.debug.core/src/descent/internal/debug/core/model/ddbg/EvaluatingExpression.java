package descent.internal.debug.core.model.ddbg;

import java.io.IOException;

import org.eclipse.debug.core.DebugException;

public class EvaluatingExpression implements IState {

	public DdbgVariable fVariable;
	private final String fExpression;
	private final DdbgDebugger fCli;
	
	public EvaluatingExpression(DdbgDebugger cli, String expression) {
		this.fCli = cli;
		this.fExpression = expression;
	}

	public void interpret(String text) throws DebugException, IOException {
		if (text.equals("->")) { //$NON-NLS-1$
			fCli.notifyStateReturn();
		} else {
			parseVariable(text);
		}
	}

	private void parseVariable(String text) {
		text = text.trim();
		
		if ("{".equals(text)) { //$NON-NLS-1$
			fVariable = new DdbgVariable(fExpression);
			return;
		}
		
		if ("}".equals(text) || "},".equals(text)) { //$NON-NLS-1$ //$NON-NLS-2$
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
		
		boolean nameIsBase = name.indexOf('.') != -1;
		
		if ("{".equals(value)) { //$NON-NLS-1$
			DdbgVariable newVariable = new DdbgVariable(name, null);
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
			
			DdbgVariable newVariable;
			
			if ("...".equals(value)) { //$NON-NLS-1$
				newVariable = new DdbgVariable(name);
				newVariable.setLazy(true);
				newVariable.setIsBase(nameIsBase);
			} else {
				newVariable = new DdbgVariable(name, value);
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
