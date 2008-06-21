package descent.internal.debug.core.model.gdb;

import java.io.IOException;

import org.eclipse.debug.core.DebugException;

public class Running implements IState {
	
	private final GdbDebugger fCli;
	
	private boolean fLastWasBreakpointHit;
	private String fBreakpointFileName;
	private int fBreakpointLineNumber;
	private boolean fNextHasBreakpointInfo;

	public Running(GdbDebugger cli) {
		this.fCli = cli;
	}

	public void interpret(String text) throws DebugException, IOException {
		if (fNextHasBreakpointInfo) {
			fNextHasBreakpointInfo = false;
			
			// at file:lineNumber address
			int indexOfColon = text.lastIndexOf(':');
			if (indexOfColon != -1) {
				if (!processBreakpointInfo(text, indexOfColon)) {
					return;
				}
			}
		}
		
		if (fLastWasBreakpointHit) {
			if (text.trim().equals("(gdb)")) {
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
			int indexOfColon = text.lastIndexOf(':');
			if (indexOfColon != -1) {
				if (!processBreakpointInfo(text, indexOfColon)) {
					return;
				}
			} else {
				// The information might be in the next line
				fNextHasBreakpointInfo = true;
			}
		}
	}
	
	private boolean processBreakpointInfo(String text, int indexOfColon) {
		// * at file:lineNumber address
		int indexOfSpaceBefore = text.lastIndexOf(' ', indexOfColon - 1);
		int indexOfSpaceAfter = text.indexOf(' ', indexOfColon + 1);
		if (indexOfSpaceAfter == -1) {
			indexOfSpaceAfter = text.length();
		}
		if (indexOfSpaceBefore != -1 && indexOfSpaceAfter != -1) {
			fBreakpointFileName = text.substring(indexOfSpaceBefore + 1, indexOfColon);
			try {
				fBreakpointLineNumber = Integer.parseInt(text.substring(indexOfColon + 1, indexOfSpaceAfter));
			} catch (NumberFormatException e) {
				return false;
			}
			fLastWasBreakpointHit = true;
		}
		
		return true;
	}
	
	public void interpretError(String text) throws DebugException, IOException {
		// Nothing to do
	}
	
	@Override
	public String toString() {
		return "default"; //$NON-NLS-1$
	}
	
}
