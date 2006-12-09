package descent.core.dom;

import descent.internal.core.dom.Version;

/**
 * A debug statement:
 * 
 * <pre>
 * debug (id) statement
 *   else else_statement
 * </pre>
 * 
 * where "(id)" is optional, as well as the else part. 
 */
public interface IDebugStatement extends IConditionalStatement {
	
	/**
	 * Returns the debug level or identifier.
	 */
	Version getVersion();

}
