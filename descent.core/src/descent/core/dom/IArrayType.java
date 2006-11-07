package descent.core.dom;

/**
 * An array type. Can be static, dynamic or associative.
 */
public interface IArrayType extends IType {
	
	/**
	 * Constant representing a dynamic array.
	 * 
	 * <pre>
	 * array[]
	 * </pre>
	 */
	int DYNAMIC_ARRAY = 1;
	
	/**
	 * Constant representing an associative array.
	 * An array type with this type can be safely cast to <code>IAssociativeArrayType</code>. 
	 */
	int ASSOCIATIVE_ARRAY = 2;
	
	/**
	 * Constant representing a static array.
	 * An array type with this type can be safely cast to <code>IStaticArrayType</code>. 
	 */
	int STATIC_ARRAY = 3;
	
	/**
	 * Returns the type of this array type. Check the constants
	 * declared in this interface.
	 */
	int getArrayTypeType();
	
	/**
	 * Returns the inner type of the array. This
	 * is "innerType[...]".
	 */
	IType getInnerType();

}
