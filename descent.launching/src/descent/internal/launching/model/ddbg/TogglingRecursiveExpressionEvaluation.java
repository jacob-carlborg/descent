package descent.internal.launching.model.ddbg;

import java.io.IOException;

import org.eclipse.debug.core.DebugException;

public class TogglingRecursiveExpressionEvaluation implements IState {
	
	private final DdbgDebugger fCli;

	public TogglingRecursiveExpressionEvaluation(DdbgDebugger cli) {
		this.fCli = cli;		
	}
	
	public void interpret(String text) throws DebugException, IOException {
		if ("->".equals(text)) {
			fCli.notifyStateReturn();
		}
	}
	
	@Override
	public String toString() {
		return "toggling recursive expression evaluation";
	}
	
}
