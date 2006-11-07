package descent.core.dom;

/**
 * A throws statement:
 * 
 * <pre>
 * throw expr;
 * </pre>
 */
public interface IThrowStatement extends IStatement {
	
	/**
	 * Returns the expression to throw.
	 */
	IExpression getExpression();

}
