package descent.debug.core;


/**
 * <p>A debugger registry allows access to the registered debuggers
 * in the <code>descent.launching.debuggers</code> extension.</p>
 * 
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @see DescentDebugPlugin#getDebuggerRegistry()
 */
public interface IDebuggerRegistry {
	
	/**
	 * Returns the registered debuggers.
	 * @return the debuggers
	 */
	IDebuggerDescriptor[] getDebuggers();
	
	/**
	 * Returns a debugger with the given id, or <code>null</code> if
	 * no debugger is registered with that id.
	 * @param id the id of a debugger
	 * @return the debugger, or <code>null</code>
	 */
	IDebuggerDescriptor findDebugger(String id);

}
