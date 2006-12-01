package descent.core.dom;

/**
 * A do while statement:
 * 
 * <pre>
 * do {
 *     body
 * } while(condition)
 * </pre>
 */
public interface IDoWhileStatement extends IStatement {
	
	/**
	 * Returns the condition.
	 */
	IExpression getCondition();
	
	/**
	 * Returns the body.
	 */
	IStatement getBody();

}
