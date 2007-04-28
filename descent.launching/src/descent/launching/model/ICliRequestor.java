package descent.launching.model;

import org.eclipse.debug.core.DebugException;

public interface ICliRequestor {
	
	void started() throws DebugException;
	
	void stepEnded() throws DebugException;
	
	void breakpointHit() throws DebugException;
	
	void breakpointHit(String fileName, int lineNumber) throws DebugException;
	
	void resumed(int detail) throws DebugException;
	
	void terminated() throws DebugException;

}
