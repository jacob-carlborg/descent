package descent.core.dom;

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
	String getName();

}
