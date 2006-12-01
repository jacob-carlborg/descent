package descent.core.dom;

/**
 * Returns a type expression.
 */
public interface ITypeExpression extends IExpression {
	
	/**
	 * Returns the type used in the expression.
	 */
	IType getType();

}
