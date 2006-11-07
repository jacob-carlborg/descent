package descent.core.dom;

/**
 * An array literal expression:
 * 
 * <pre>
 * [arg1, arg2, ..., argN]
 * </pre>
 */
public interface IArrayLiteralExpression extends IExpression {
	
	/**
	 * Returns the arguments.
	 */
	IExpression[] getArguments();

}
