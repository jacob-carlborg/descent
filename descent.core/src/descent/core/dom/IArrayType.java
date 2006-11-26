package descent.core.dom;

/**
 * An array type. Can be static, dynamic or associative.
 */
public interface IArrayType extends IType {
	
	/**
	 * Returns the inner type of the array. This
	 * is "innerType[...]".
	 */
	IType getInnerType();

}
