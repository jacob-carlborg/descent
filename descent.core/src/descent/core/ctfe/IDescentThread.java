package descent.core.ctfe;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IThread;

public interface IDescentThread extends IThread {
	
	void stepInto(int stackFrame) throws DebugException;
	
	void stepOver(int stackFrame) throws DebugException;
	
	void stepReturn(int stackFrame) throws DebugException;

}
