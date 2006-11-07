package descent.core.dom;

/**
 * A while statement:
 * 
 * <pre>
 * while(condition) {
 *     body
 * }
 * </pre>
 */
public interface IWhileStatement extends IStatement {
	
	/**
	 * Returns the condition.
	 */
	IExpression getCondition();
	
	/**
	 * Returns the body of the while statement.
	 */
	IStatement getBody();

}
