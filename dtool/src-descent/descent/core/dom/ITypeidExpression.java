package descent.core.dom;

/**
 * A typeid expression:
 * 
 * <pre>
 * typeid(type)
 * </pre>
 */
public interface ITypeidExpression extends IExpression {
	
	/**
	 * Returns the type of the typeid.
	 */
	IType getType();

}
