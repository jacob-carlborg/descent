package descent.launching.model.ddbg;

import java.io.IOException;

import org.eclipse.debug.core.DebugException;

public class SettingStackFrame implements IState {
	
	private final DdbgDebugger fCli;

	public SettingStackFrame(DdbgDebugger cli) {
		this.fCli = cli;
	}
	
	public void interpret(String text) throws DebugException, IOException {
		if ("->".equals(text)) {
			fCli.notifyStateReturn();
		}
	}
	
	@Override
	public String toString() {
		return "setting stack frame";
	}
	
}