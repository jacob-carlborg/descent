package descent.internal.debug.core.model.ddbg;

import java.io.IOException;

import org.eclipse.debug.core.DebugException;

public class WaitingConfirmation implements IState {
	
	private final DdbgDebugger fCli;

	public WaitingConfirmation(DdbgDebugger cli) {
		this.fCli = cli;		
	}
	
	public void interpret(String text) throws DebugException, IOException {
		if ("->".equals(text)) { //$NON-NLS-1$
			fCli.notifyStateReturn();
		}
	}
	
	@Override
	public String toString() {
		return "waiting confirmation"; //$NON-NLS-1$
	}
	
}
