package descent.core.dom;

/**
 * A switch statement:
 * 
 * <pre>
 * switch(expr) {
 * 
 * }
 * </pre>
 */
public interface ISwitchStatement extends IStatement {
	
	/**
	 * Returns the expression to switch.
	 */
	IExpression getExpression();
	
	/**
	 * Returns the body of this statement.
	 */
	IStatement getBody();

}
