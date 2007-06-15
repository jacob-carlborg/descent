package descent.core.dom;

/**
 * A for statement:
 * 
 * <pre>
 * for(initializer; condition; increment) {
 *     body
 * }
 * </pre>
 */
public interface IForStatement extends IDescentStatement {
	
	/**
	 * Returns the initializer. May be null.
	 */
	IDescentStatement getInitializer();
	
	/**
	 * Returns the condition. May be null.
	 */
	IExpression getCondition();
	
	/**
	 * Returns the increment. May be null.
	 */
	IExpression getIncrement();

	/**
	 * Returns the body of the for.
	 */
	IDescentStatement getBody();

}
