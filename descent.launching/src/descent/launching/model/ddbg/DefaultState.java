package descent.launching.model.ddbg;

import java.io.IOException;

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
			int indexOfColon = text.lastIndexOf(':');
			if (indexOfColon != -1) {
				int indexOfSpaceBefore = text.lastIndexOf(' ', indexOfColon - 1);
				int indexOfSpaceAfter = text.indexOf(' ', indexOfColon + 1);
				if (indexOfSpaceBefore != -1 && indexOfSpaceAfter != -1) {
					String fileName = text.substring(indexOfSpaceBefore + 1, indexOfColon);
					int lineNumber = Integer.parseInt(text.substring(indexOfColon + 1, indexOfSpaceAfter));
					fCli.fCliRequestor.breakpointHit(fileName, lineNumber);
					return;
				}
			}
			
			fCli.fCliRequestor.breakpointHit();
		}
	}
	
	@Override
	public String toString() {
		return "default";
	}
	
}
