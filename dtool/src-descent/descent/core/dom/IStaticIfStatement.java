package descent.core.dom;

/**
 * A static if statement:
 * 
 * <pre>
 * static if (expr) statement
 *   else else_statement
 * </pre>
 * 
 * where the else part is optional. 
 */
public interface IStaticIfStatement extends IConditionalStatement {
	
	/**
	 * The static condition to evaluate.
	 */
	IExpression getCondition();

}
