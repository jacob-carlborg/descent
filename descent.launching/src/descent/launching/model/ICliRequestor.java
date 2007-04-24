package descent.launching.model;

import org.eclipse.debug.core.DebugException;

public interface ICliRequestor {
	
	void started() throws DebugException;
	
	void suspended(int detail) throws DebugException;
	
	void resumed(int detail) throws DebugException;
	
	void terminated() throws DebugException;

}
