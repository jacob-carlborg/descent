package descent.internal.launching.model.gdb;

import java.io.IOException;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;

public class Stepping implements IState {
	
	private final GdbDebugger fCli;
	private final int fDebugEvent;
	
	public Stepping(GdbDebugger cli, int debugEvent) {
		this.fCli = cli;
		this.fDebugEvent = debugEvent;
		try {
			fCli.fListener.resumed(debugEvent);
		} catch (DebugException e) {
			e.printStackTrace();
		}
	}
	
	public void interpret(String text) throws DebugException, IOException {
		if ("Process terminated".equals(text)) {
			fCli.fListener.terminated();
			fCli.notifyStateReturn();
		} else if ("(gdb)".equals(text)) {
			fCli.fListener.stepEnded();
			fCli.notifyStateReturn();
		}
	}
	
	public void interpretError(String text) throws DebugException, IOException {
		// Nothing to do
	}
	
	@Override
	public String toString() {
		switch(fDebugEvent) {
		case DebugEvent.STEP_INTO:
			return "stepping into";
		case DebugEvent.STEP_OVER:
			return "stepping over";
		case DebugEvent.STEP_RETURN:
			return "stepping return";
		}
		return super.toString();
	}

}
