package descent.core.dom;

/**
 * A static array type:
 * 
 * <pre>
 * array[dimension]
 * </pre>
 */
public interface IStaticArrayType extends IArrayType {
	
	/**
	 * Returns the dimension of the array.
	 */
	IExpression getSize();

}
