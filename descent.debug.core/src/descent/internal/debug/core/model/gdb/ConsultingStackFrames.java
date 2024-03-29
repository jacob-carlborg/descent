package descent.internal.debug.core.model.gdb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IStackFrame;

public class ConsultingStackFrames  implements IState {
	
	private final GdbDebugger fCli;

	private StringBuilder fBuffer;
	public List<IStackFrame> fStackFrames = new ArrayList<IStackFrame>();
	
	public ConsultingStackFrames(GdbDebugger cli) {
		this.fCli = cli;
		this.fBuffer = new StringBuilder();
	}
	
	public void interpret(String text) throws DebugException, IOException {
		// Fix
		if (text.startsWith("(gdb)#")) { //$NON-NLS-1$
			text = text.substring(2);
		}
		if (text.equals("(gdb)")) { //$NON-NLS-1$
			if (fBuffer.length() > 0) {
				fStackFrames.add(parseStackFrame(fBuffer.toString()));
			}
			fCli.notifyStateReturn();
		} else if (text.startsWith("#")) { //$NON-NLS-1$
			if (fBuffer.length() > 0) {
				fStackFrames.add(parseStackFrame(fBuffer.toString()));
				fBuffer.setLength(0);	
			}
			fBuffer.append(text.trim());
		} else {
			fBuffer.append(text.trim());
		}
	}
	
	public void interpretError(String text) throws DebugException, IOException {
		// Nothing to do
	}
	
	private IStackFrame parseStackFrame(String data) {
		String name = data;
		int lineNumber = -1;
		int number = -1;
		String sourceName = null;
			
		if (data.length() == 0 || data.charAt(0) != '#') {
			return fCli.fFactory.newStackFrame(name, number, sourceName, lineNumber);
		}
		
		// Some positions in the string
		int indexOfFirstSpace = data.indexOf(' ');
		int indexOfFirstParen = data.indexOf("("); //$NON-NLS-1$
		int indexOfIn = data.indexOf(" in "); //$NON-NLS-1$
		int indexOfFrom = data.lastIndexOf(" from "); //$NON-NLS-1$
		int indexOfAt = data.lastIndexOf(" at "); //$NON-NLS-1$
		if (indexOfAt == -1) {
			// It may also come like this
			indexOfAt = data.lastIndexOf(")at "); //$NON-NLS-1$
		}
		int lastIndexOfColon = data.lastIndexOf(':');
		
		// Number
		number = Integer.parseInt(data.substring(1, indexOfFirstSpace));		
		
		// Name
		if (indexOfIn != -1 && indexOfFirstParen != -1 && indexOfIn < indexOfFirstParen) {
			// in ... <name> ... from
			name = data.substring(indexOfIn + 4, indexOfFirstParen - 1).trim() + "()"; //$NON-NLS-1$
		} else if (indexOfIn != -1 && indexOfFrom != -1 && indexOfIn < indexOfFrom) {
			// in ... <name> ... from
			name = data.substring(indexOfIn + 4, indexOfFrom + 1);
		} else if (indexOfIn != -1 && indexOfAt != -1 && indexOfIn < indexOfAt) {
			// in ... <name> ... at
			name = data.substring(indexOfIn + 4, indexOfAt + 1);
		} else if (indexOfIn != -1 && indexOfFirstParen != -1 && indexOfIn < indexOfFirstParen) {
			// in ... <name> ... (
			name = data.substring(indexOfIn + 4, indexOfFirstParen - 1).trim() + "()"; //$NON-NLS-1$
		} else if (indexOfFirstSpace != -1 && indexOfFirstParen != -1 && indexOfFirstSpace < indexOfFirstParen) {
			// ... <name> ... (
			name = data.substring(indexOfFirstSpace + 1, indexOfFirstParen - 1).trim() + "()"; //$NON-NLS-1$
		} else {
			if (indexOfFirstSpace != -1) {
				if (indexOfAt != -1) {
					//  ... <name> ... at
					name = data.substring(indexOfFirstSpace + 1, indexOfAt + 1);
				} else {
					// ... <name> ...
					int indexOfSecondSpace = data.indexOf(' ', indexOfFirstSpace + 1);
					if (indexOfSecondSpace != -1) {
						name = data.substring(indexOfFirstSpace + 1, indexOfSecondSpace);
					}
				}
			}
		}
		
		name = name.trim();
		if (name.endsWith(" ()")) { //$NON-NLS-1$
			name = name.substring(0, name.length() - 3) + "()"; //$NON-NLS-1$
		}
		
		
		// sourceName and lineNumber
		if (indexOfAt != -1 && lastIndexOfColon != -1) {
			sourceName = data.substring(indexOfAt + 4, lastIndexOfColon);
			lineNumber = Integer.parseInt(data.substring(lastIndexOfColon + 1));
		}
		
		return fCli.fFactory.newStackFrame(name, number, sourceName, lineNumber);
	}
	
	@Override
	public String toString() {
		return "consulting stack frame"; //$NON-NLS-1$
	}
	
}
