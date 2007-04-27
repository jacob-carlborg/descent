package descent.launching.model.ddbg;

import java.io.IOException;

import org.eclipse.debug.core.DebugException;

public class ConsultingType implements IState {
	
	private final DdbgCli fCli;
	public String fType;
	
	public ConsultingType(DdbgCli cli) {
		this.fCli = cli;
	}

	public void interpret(String text) throws DebugException, IOException {
		if ("->".equals(text)) {
			fCli.notifyStateReturn();
		} else {
			fType = text;
		}
	}

}
