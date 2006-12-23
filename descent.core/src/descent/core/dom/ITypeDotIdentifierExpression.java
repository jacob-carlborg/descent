package descent.core.dom;

/**
 * A type dot identifier expression:
 * 
 * <pre>
 * type.property
 * </pre>
 */
public interface ITypeDotIdentifierExpression extends IExpression {
	
	/**
	 * Returns the type.
	 */
	IType getType();
	
	/**
	 * Returns the property.
	 */
	ISimpleName getName();

}
