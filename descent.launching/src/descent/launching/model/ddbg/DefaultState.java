package descent.launching.model.ddbg;

import java.io.IOException;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;

public class DefaultState implements IState {
	
	private final DdbgCli fCli;

	public DefaultState(DdbgCli cli) {
		this.fCli = cli;
	}

	public void interpret(String text) throws DebugException, IOException {
		if (text.equals("Process terminated")) {
			fCli.fCliRequestor.terminated();
		} else if (text.startsWith("Breakpoint ")) {
			// Breakpoint n hit at file:lineNumber address
			fCli.fCliRequestor.suspended(DebugEvent.BREAKPOINT);
		}
	}
	
	@Override
	public String toString() {
		return "default";
	}
	
}
