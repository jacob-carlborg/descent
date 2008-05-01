package descent.internal.debug.core.model.ddbg;

import java.io.IOException;

import org.eclipse.debug.core.DebugException;

public class Running implements IState {
	
	private final DdbgDebugger fCli;
	
	private int fLastWasBreakpointHit = 0;
	private String fBreakpointFileName;
	private int fBreakpointLineNumber;

	public Running(DdbgDebugger cli) {
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
		if (text.equals("Process terminated")) { //$NON-NLS-1$
			fCli.fListener.terminated();
		} else if (text.startsWith("Breakpoint ")) { //$NON-NLS-1$
			// Breakpoint n hit at file:lineNumber address
			
			// After a breakpoint hit comes the source, then "->". So we signal
			// the breakpoint hit in two next interpret call.
			fLastWasBreakpointHit = 2;
			handleBreakpoint(text);
			
		// Unhandled exceptions create breakpoints
		} else if (text.startsWith("Unhandled Exception")) { //$NON-NLS-1$
			// Unhandled Exception: EXCEPTION_ACCESS_VIOLATION(0xc0000005) at other.foo other.d:9 (0x00402035) thread(3856)
			
			fLastWasBreakpointHit = 1;			
			handleBreakpoint(text);
		}
	}
	
	private void handleBreakpoint(String text) {
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
	
	@Override
	public String toString() {
		return "running"; //$NON-NLS-1$
	}
	
}
