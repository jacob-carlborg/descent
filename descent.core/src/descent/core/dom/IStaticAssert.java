package descent.core.dom;

/**
 * A static assert:
 * 
 * <pre>
 * static assert(expr, message);
 * </pre>
 */
public interface IStaticAssert extends IDElement {
	
	/**
	 * Returns the expression to assert.
	 */
	IExpression getExpression();
	
	/**
	 * Returns the message in case the assert doesn't hold.
	 */
	IExpression getMessage();

}
