package descent.core.dom;

/**
 * A new expression:
 * 
 * <pre>
 * new type(arg1, arg2, ..., argN)
 * </pre>
 */
public interface INewExpression extends IExpression {
	
	/**
	 * Returns the type to create.
	 */
	IType getType();
	
	/**
	 * Returns the arguments of the expression.
	 */
	IExpression[] getArguments();

}
