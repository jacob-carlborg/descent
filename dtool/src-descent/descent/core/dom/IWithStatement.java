package descent.core.dom;

/**
 * A with statement:
 * 
 * <pre>
 * with(expr) {
 * 
 * }
 * </pre>
 */
public interface IWithStatement extends IDescentStatement {
	
	/**
	 * Returns the expression to use as with.
	 */
	IExpression getExpression();
	
	/**
	 * Returns the statement used as a body.
	 */
	IDescentStatement getStatement();

}
