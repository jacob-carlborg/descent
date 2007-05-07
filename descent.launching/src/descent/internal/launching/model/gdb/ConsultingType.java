package descent.internal.launching.model.gdb;

import java.io.IOException;

import org.eclipse.debug.core.DebugException;

public class ConsultingType implements IState {
	
	private final GdbDebugger fCli;
	public String fType;
	
	public ConsultingType(GdbDebugger cli) {
		this.fCli = cli;
	}

	public void interpret(String text) throws DebugException, IOException {
		if ("(gdb)".equals(text)) {
			fCli.notifyStateReturn();
		} else {			
			int indexOf = text.indexOf("type = ");
			if (indexOf != -1 && indexOf + 7 < text.length()) {
				fType = text.substring(indexOf + 7).trim();
			} else {
				fType = text;
			}
		}
	}
	
	public void interpretError(String text) throws DebugException, IOException {
		// Nothing to do
	}

}
