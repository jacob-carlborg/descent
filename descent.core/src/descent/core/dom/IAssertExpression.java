package descent.core.dom;

/**
 * An assert expression:
 * 
 * <pre>
 * assert(expr, message)
 * </pre>
 */
public interface IAssertExpression extends IExpression {
	
	/**
	 * Returns the expression to assert.
	 */
	IExpression getExpression();
	
	/**
	 * Returns the message in case the assert dosen't hold true.
	 */
	IExpression getMessage();

}
