package descent.internal.launching.model.ddbg;

import java.io.IOException;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;

public class Stepping implements IState {
	
	private final DdbgDebugger fCli;
	private final int fDebugEvent;
	
	public Stepping(DdbgDebugger cli, int debugEvent) {
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
		} else if ("->".equals(text)) {
			fCli.fListener.stepEnded();
			fCli.notifyStateReturn();
		}
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
