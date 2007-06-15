package descent.core.dom;

/**
 * A static assert statement:
 * 
 * <pre>
 * static assert(expr, message);
 * </pre>
 */
public interface IStaticAssertStatement extends IDescentStatement {
	
	/**
	 * Returns the expression to assert.
	 */
	IExpression getExpression();
	
	/**
	 * Returns the message in case the assert doesn't hold.
	 */
	IExpression getMessage();

}
