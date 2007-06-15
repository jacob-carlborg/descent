package descent.core.dom;

/**
 * A case statement:
 * 
 * <pre>
 * case expr: statement
 * </pre>
 */
public interface ICaseStatement extends IDescentStatement {
	
	/**
	 * Returns the expression.
	 */
	IExpression getExpression();

	/**
	 * Returns the statement.
	 */
	IDescentStatement getStatement();

}
