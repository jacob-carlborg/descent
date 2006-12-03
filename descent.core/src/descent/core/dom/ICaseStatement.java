package descent.core.dom;

/**
 * A case statement:
 * 
 * <pre>
 * case expr: statement
 * </pre>
 */
public interface ICaseStatement extends IStatement {
	
	/**
	 * Returns the expression.
	 */
	IExpression getExpression();

	/**
	 * Returns the statement.
	 */
	IStatement getBody();

}
