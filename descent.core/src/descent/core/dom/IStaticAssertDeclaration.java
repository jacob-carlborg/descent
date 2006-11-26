package descent.core.dom;

/**
 * A static assert:
 * 
 * <pre>
 * static assert(expr, message);
 * </pre>
 */
public interface IStaticAssertDeclaration extends IDeclaration {
	
	/**
	 * Returns the expression to assert.
	 */
	IExpression getExpression();
	
	/**
	 * Returns the message in case the assert doesn't hold.
	 */
	IExpression getMessage();

}
