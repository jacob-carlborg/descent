package descent.launching.model.gdb;

import java.io.IOException;

import org.eclipse.debug.core.DebugException;

public class RemovingBreakpoint implements IState {
	
	private final GdbDebugger fCli;

	public RemovingBreakpoint(GdbDebugger cli) {
		this.fCli = cli;		
	}
	
	public void interpret(String text) throws DebugException, IOException {
		if ("(gdb) ".equals(text)) {
			fCli.notifyStateReturn();
		}
	}
	
	public void interpretError(String text) throws DebugException, IOException {
		// Nothing to do
	}
	
	@Override
	public String toString() {
		return "removing breakpoint";
	}
	
}
