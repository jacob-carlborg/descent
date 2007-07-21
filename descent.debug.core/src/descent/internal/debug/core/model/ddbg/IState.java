package descent.internal.debug.core.model.ddbg;

import java.io.IOException;

import org.eclipse.debug.core.DebugException;

public interface IState {
	
	void interpret(String text) throws DebugException, IOException;

}
