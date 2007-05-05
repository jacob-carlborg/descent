package descent.launching.model.gdb;

import java.io.IOException;

import org.eclipse.debug.core.DebugException;

public class RemovingBreakpoint implements IState {
	
	private final GdbCli fCli;

	public RemovingBreakpoint(GdbCli cli) {
		this.fCli = cli;		
	}
	
	public void interpret(String text) throws DebugException, IOException {
		if ("(gdb) ".equals(text)) {
			fCli.notifyStateReturn();
		}
	}
	
	@Override
	public String toString() {
		return "removing breakpoint";
	}
	
}
