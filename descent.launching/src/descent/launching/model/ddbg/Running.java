package descent.launching.model.ddbg;

import java.io.IOException;

import org.eclipse.debug.core.DebugException;

public class Running implements IState {
	
	private final DdbgCli fCli;
	
	private int fLastWasBreakpointHit = 0;
	private String fBreakpointFileName;
	private int fBreakpointLineNumber;

	public Running(DdbgCli cli) {
		this.fCli = cli;
	}

	public void interpret(String text) throws DebugException, IOException {
		if (fLastWasBreakpointHit > 0) {
			fLastWasBreakpointHit--;
			if (fLastWasBreakpointHit == 0) {
				if (fBreakpointFileName != null) {
					fCli.fCliRequestor.breakpointHit(fBreakpointFileName, fBreakpointLineNumber);
				} else {
					fCli.fCliRequestor.breakpointHit();
				}
			}
		}
		if (text.equals("Process terminated")) {
			fCli.fCliRequestor.terminated();
		} else if (text.startsWith("Breakpoint ")) {
			// Breakpoint n hit at file:lineNumber address
			
			// After a breakpoint hit comes the source, then "->". So we signal
			// the breakpoint hit in two next interpret call.
			fLastWasBreakpointHit = 2;
			
			int indexOfColon = text.lastIndexOf(':');
			if (indexOfColon != -1) {
				int indexOfSpaceBefore = text.lastIndexOf(' ', indexOfColon - 1);
				int indexOfSpaceAfter = text.indexOf(' ', indexOfColon + 1);
				if (indexOfSpaceBefore != -1 && indexOfSpaceAfter != -1) {
					fBreakpointFileName = text.substring(indexOfSpaceBefore + 1, indexOfColon);
					fBreakpointLineNumber = Integer.parseInt(text.substring(indexOfColon + 1, indexOfSpaceAfter));
					return;
				}
			}
		}
	}
	
	@Override
	public String toString() {
		return "default";
	}
	
}
