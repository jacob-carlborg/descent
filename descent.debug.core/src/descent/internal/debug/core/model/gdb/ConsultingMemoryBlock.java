package descent.internal.debug.core.model.gdb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.debug.core.DebugException;

public class ConsultingMemoryBlock implements IState {
	
	private final GdbDebugger fCli;
	private final long length;
	
	private List<String> fLines;
	
	public byte[] fBytes;

	public ConsultingMemoryBlock(GdbDebugger cli, long length) {
		this.fCli = cli;
		this.length = length;
		this.fLines = new ArrayList<String>();
	}

	public void interpret(String text) throws DebugException, IOException {
		if ("(gdb)".equals(text)) { //$NON-NLS-1$
			createMemoryBlock();
			fCli.notifyStateReturn();
		} else {
			fLines.add(text);
		}
	}
	
	public void interpretError(String text) throws DebugException, IOException {
		// Nothing to do
	}

	private void createMemoryBlock() {
		if (fLines.isEmpty()) {
			// Should not happen, anyway
			fBytes = new byte[0];
		} else {
			fBytes = new byte[(int) length];
			int byteNum = 0;
			
			
			for(String line : fLines) {
				String[] blocks = line.split("\\t"); //$NON-NLS-1$
				
				// Skip the first: the address
				for(int i = 1; i < blocks.length; i++) {
					String block = blocks[i].trim();
					
					if (block.length() != 0) {
						if (block.startsWith("0x") || block.startsWith("0X")) { //$NON-NLS-1$ //$NON-NLS-2$
							fBytes[byteNum] = (byte) Integer.parseInt(block.substring(2), 16);
						} else {
							fBytes[byteNum] = (byte) Integer.parseInt(block);
						}
						byteNum++;
					}					
				}		
			}
		}
	}
	
	

}
