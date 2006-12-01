package descent.core.dom;

/**
 * A call expression:
 * 
 * <pre>
 * expr(arg1, arg2, ..., argN)
 * </pre>
 *
 */
public interface ICallExpression extends IExpression {
	
	/**
	 * Returns the expression on which the call is made.
	 */
	IExpression getExpression();
	
	/**
	 * Returns the arguments of the call.
	 */
	IExpression[] getArguments();

}
