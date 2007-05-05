package descent.launching.model.gdb;

import java.io.IOException;

import org.eclipse.debug.core.DebugException;

public class ConsultingType implements IState {
	
	private final GdbCli fCli;
	public String fType;
	
	public ConsultingType(GdbCli cli) {
		this.fCli = cli;
	}

	public void interpret(String text) throws DebugException, IOException {
		if ("(gdb) ".equals(text)) {
			fCli.notifyStateReturn();
		} else {
			fType = text.substring("type = ".length()).trim();
		}
	}
	
	public void interpretError(String text) throws DebugException, IOException {
		// Nothing to do
	}

}
