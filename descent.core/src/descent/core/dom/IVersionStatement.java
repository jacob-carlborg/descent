package descent.core.dom;

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
	String getName();

}
