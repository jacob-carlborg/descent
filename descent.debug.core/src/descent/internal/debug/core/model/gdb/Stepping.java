package descent.internal.debug.core.model.gdb;

import java.io.IOException;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;

public class Stepping implements IState {
	
	private final GdbDebugger fCli;
	private final int fDebugEvent;
	private final boolean fWaitJustOne;
	
	public Stepping(GdbDebugger cli, int debugEvent) {
		this(cli, debugEvent, false);
	}
	
	public Stepping(GdbDebugger cli, int debugEvent, boolean waitJustOne) {
		this.fCli = cli;
		this.fDebugEvent = debugEvent;
		this.fWaitJustOne = waitJustOne;
		try {
			fCli.fListener.resumed(debugEvent);
		} catch (DebugException e) {
			e.printStackTrace();
		}
	}
	
	public void interpret(String text) throws DebugException, IOException {
		if (text.startsWith("Process terminated")) { //$NON-NLS-1$
			fCli.fListener.terminated();
			fCli.notifyStateReturn();
		} else {
			if (fWaitJustOne) {
				fCli.notifyStateReturn();
				return;
			}
			
			if ("(gdb)".equals(text)) { //$NON-NLS-1$
				fCli.fListener.stepEnded();
				fCli.notifyStateReturn();
			}
		}
	}
	
	public void interpretError(String text) throws DebugException, IOException {
		// Nothing to do
	}
	
	@Override
	public String toString() {
		switch(fDebugEvent) {
		case DebugEvent.STEP_INTO:
			return "stepping into"; //$NON-NLS-1$
		case DebugEvent.STEP_OVER:
			return "stepping over"; //$NON-NLS-1$
		case DebugEvent.STEP_RETURN:
			return "stepping return"; //$NON-NLS-1$
		}
		return super.toString();
	}

}
