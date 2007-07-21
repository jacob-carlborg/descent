package descent.debug.core;

import org.eclipse.core.runtime.CoreException;

import descent.debug.core.model.IDebugger;

/**
 * Base interface for debuggers defined via extension points.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 */
public interface IDebuggerDescriptor {
	
	/**
	 * Returns the id.
	 * @return the id
	 */
	String getId();
	
	/**
	 * Returns the name.
	 * @return the name
	 */
	String getName();
	
	/**
	 * Creates a debugger.
	 * @return a debugger
	 * @throws CoreException thrown if there is a problem creating the debugger
	 */
	IDebugger createDebugger() throws CoreException;

}
