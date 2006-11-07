package descent.core.dom;

/**
 * An array expression:
 * 
 * <pre>
 * expr[arg1, arg2, ..., argN]
 * </pre>
 */
public interface IArrayExpression extends IExpression {
	
	/**
	 * Returns the expression on which the array is operating.
	 */
	IExpression getExpression();
	
	/**
	 * Returns the arguments of the array.
	 */
	IExpression[] getArguments();

}
