package descent.core.dom;

/**
 * A typeof type:
 * 
 * <pre>
 * typeof(expr)
 * </pre>
 */
public interface ITypeofType extends IType {
	
	/**
	 * Returns the expression of this typeof.
	 */
	IExpression getExpression();

}
