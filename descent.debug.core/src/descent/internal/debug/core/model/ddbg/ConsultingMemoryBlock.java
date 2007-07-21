package descent.internal.debug.core.model.ddbg;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.debug.core.DebugException;

public class ConsultingMemoryBlock implements IState {
	
	private final DdbgDebugger fCli;
	private final long length;
	
	private List<String> fLines;
	
	public byte[] fBytes;

	public ConsultingMemoryBlock(DdbgDebugger cli, long length) {
		this.fCli = cli;
		this.length = length;
		this.fLines = new ArrayList<String>();
	}

	public void interpret(String text) throws DebugException, IOException {
		if ("->".equals(text)) {
			createMemoryBlock();
			fCli.notifyStateReturn();
		} else {
			fLines.add(text);
		}
	}

	private void createMemoryBlock() {
		if (fLines.isEmpty()) {
			// Should not happen, anyway
			fBytes = new byte[0];
		} else {
			fBytes = new byte[(int) length];
			int byteNum = 0;
			
			for(String line : fLines) {
				int lineLength = line.length();
				
				// Skip the first 10 chars: the address
				for(int i = 10; i < lineLength; ) {
					for(int b = 0; b < 4 && i < lineLength; b++, i+=2) {
						String aByte = line.substring(i, i + 2);
						fBytes[byteNum] = (byte) Integer.parseInt(aByte, 16);
						byteNum++;
					}
					// Skip the separator
					i++;
				}				
			}
		}
	}
	
	

}
