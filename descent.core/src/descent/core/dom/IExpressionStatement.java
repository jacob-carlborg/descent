package descent.core.dom;

/**
 * An expression statement:
 * 
 * <pre>
 * expr;
 * </pre>
 */
public interface IExpressionStatement extends IStatement {
	
	/**
	 * Returns the expression.
	 */
	IExpression getExpression();

}
