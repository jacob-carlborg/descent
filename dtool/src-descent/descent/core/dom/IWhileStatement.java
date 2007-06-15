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
public interface IWhileStatement extends IDescentStatement {
	
	/**
	 * Returns the condition.
	 */
	IExpression getCondition();
	
	/**
	 * Returns the body of the while statement.
	 */
	IDescentStatement getBody();

}
