package descent.core.dom;

/**
 * A dynamic array:
 * 
 * <pre>
 * array[key]
 * </pre>
 */
public interface IAssociativeArrayType extends IArrayType {
	
	/**
	 * Returns the key type of the associative array.
	 */
	IType getKeyType();

}
