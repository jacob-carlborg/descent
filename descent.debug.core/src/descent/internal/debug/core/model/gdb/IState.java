package descent.internal.debug.core.model.gdb;

import java.io.IOException;

import org.eclipse.debug.core.DebugException;

public interface IState {
	
	void interpret(String text) throws DebugException, IOException;
	
	void interpretError(String text) throws DebugException, IOException;

}
