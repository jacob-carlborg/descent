package descent.core.dom;

import descent.internal.core.dom.Version;

/**
 * A version statement:
 * 
 * <pre>
 * version (id) statement
 *   else else_statement
 * </pre>
 * 
 * where "(id)" is optional, as well as the else part. 
 */
public interface IVersionStatement extends IConditionalStatement {
	
	/**
	 * Returns the version number or identifier.
	 */
	Version getVersion();

}
