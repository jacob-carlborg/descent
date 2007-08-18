package descent.internal.debug.core.model.gdb;

import java.io.IOException;

import org.eclipse.debug.core.DebugException;

public class Running implements IState {
	
	private final GdbDebugger fCli;
	
	private int fLastWasBreakpointHit = 0;
	private String fBreakpointFileName;
	private int fBreakpointLineNumber;

	public Running(GdbDebugger cli) {
		this.fCli = cli;
	}

	public void interpret(String text) throws DebugException, IOException {
		if (fLastWasBreakpointHit > 0) {
			fLastWasBreakpointHit--;
			if (fLastWasBreakpointHit == 0) {
				if (fBreakpointFileName != null) {
					fCli.fListener.breakpointHit(fBreakpointFileName, fBreakpointLineNumber);
				} else {
					fCli.fListener.breakpointHit();
				}
			}
		}
		if (text.trim().equals("Program exited normally.")) { //$NON-NLS-1$
			fCli.fListener.terminated();
		} else if (text.startsWith("Breakpoint ")) { //$NON-NLS-1$
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
	
	public void interpretError(String text) throws DebugException, IOException {
		// Nothing to do
	}
	
	@Override
	public String toString() {
		return "default"; //$NON-NLS-1$
	}
	
}
