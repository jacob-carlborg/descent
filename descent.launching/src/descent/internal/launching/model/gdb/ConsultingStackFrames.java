package descent.internal.launching.model.gdb;

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
		if (text.startsWith("(gdb)#")) {
			text = text.substring(2);
		}
		if (text.equals("(gdb)")) {
			if (fBuffer.length() > 0) {
				fStackFrames.add(parseStackFrame(fBuffer.toString()));
			}
			fCli.notifyStateReturn();
		} else if (text.startsWith("#")) {
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
		int indexOfFirstParen = data.indexOf("(");
		int indexOfIn = data.indexOf(" in ");
		int indexOfFrom = data.lastIndexOf(" from ");
		int indexOfAt = data.lastIndexOf(" at ");
		int lastIndexOfColon = data.lastIndexOf(':');
		
		// Number
		number = Integer.parseInt(data.substring(1, indexOfFirstSpace));		
		
		// Name
		if (indexOfIn != -1 && indexOfFirstParen != -1 && indexOfIn < indexOfFirstParen) {
			// in ... <name> ... from
			name = data.substring(indexOfIn + 4, indexOfFirstParen - 1).trim() + "()";
		} else if (indexOfIn != -1 && indexOfFrom != -1 && indexOfIn < indexOfFrom) {
			// in ... <name> ... from
			name = data.substring(indexOfIn + 4, indexOfFrom + 1);
		} else if (indexOfIn != -1 && indexOfAt != -1 && indexOfIn < indexOfAt) {
			// in ... <name> ... at
			name = data.substring(indexOfIn + 4, indexOfAt + 1);
		} else if (indexOfIn != -1 && indexOfFirstParen != -1 && indexOfIn < indexOfFirstParen) {
			// in ... <name> ... (
			name = data.substring(indexOfIn + 4, indexOfFirstParen - 1).trim() + "()";
		} else if (indexOfFirstSpace != -1 && indexOfFirstParen != -1 && indexOfFirstSpace < indexOfFirstParen) {
			// ... <name> ... (
			name = data.substring(indexOfFirstSpace + 1, indexOfFirstParen - 1).trim() + "()";
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
		if (name.endsWith(" ()")) {
			name = name.substring(0, name.length() - 3) + "()";
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
		return "consulting stack frame";
	}
	
}
