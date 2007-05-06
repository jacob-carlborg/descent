package descent.launching.model.ddbg;

import java.io.IOException;

import org.eclipse.debug.core.DebugException;

public class RemovingBreakpoint implements IState {
	
	private final DdbgDebugger fCli;

	public RemovingBreakpoint(DdbgDebugger cli) {
		this.fCli = cli;		
	}
	
	public void interpret(String text) throws DebugException, IOException {
		if ("->".equals(text)) {
			fCli.notifyStateReturn();
		}
	}
	
	@Override
	public String toString() {
		return "removing breakpoint";
	}
	
}