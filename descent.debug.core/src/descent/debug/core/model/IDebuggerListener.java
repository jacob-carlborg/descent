package descent.debug.core.model;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;

/**
 * Listens events notified by an {@link IDebugger}.
 * 
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 */
public interface IDebuggerListener {
	
	/**
	 * Notifies this listener that the debuggee program has started.
	 */
	void started() throws DebugException;
	
	/**
	 * Notifies this listener that the debuggee program has ended a step.
	 */
	void stepEnded() throws DebugException;
	
	/**
	 * Notifies this listener that an unknown breakpoint was hit.
	 */
	void breakpointHit() throws DebugException;
	
	/**
	 * Notifies this listener that a breakpoint was hit in the given fileName,
	 * at the given lineNumber.
	 * @param fileName the file number
	 * @param lineNumber the line number
	 * @throws DebugException
	 */
	void breakpointHit(String fileName, int lineNumber) throws DebugException;
	
	/**
	 * Notifies this listener that the debuggee resumed it's execution.
	 * @param detail one of the {@link DebugEvent} constants: 
	 * {@link DebugEvent#STEP_IN}, {@link DebugEvent#STEP_OVER}, {@link DebugEvent#STEP_RETURN}.
	 * @throws DebugException
	 */
	void resumed(int detail) throws DebugException;
	
	/**
	 * Notifies this listener that the debuggee program has terminated.
	 */
	void terminated() throws DebugException;

}
