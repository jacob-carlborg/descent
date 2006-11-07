package descent.core.dom;

/**
 * A type in the D language.
 */
public interface IType extends IDElement {
	
	/**
	 * Constant representing a basic type.
	 * A type with this type can be safely cast to <code>IBasicType</code>. 
	 */
	int TYPE_BASIC = 1;
	
	/**
	 * Constant representing a pointer type.
	 * A type with this type can be safely cast to <code>IPointerType</code>. 
	 */
	int TYPE_POINTER = 2;
	
	/**
	 * Constant representing an array type.
	 * A type with this type can be safely cast to <code>IArrayType</code>. 
	 */
	int TYPE_ARRAY = 3;
	
	/**
	 * Constant representing a template instance type.
	 * A type with this type can be safely cast to <code>ITemplateInstanceType</code>. 
	 */
	int TYPE_TEMPLATE_INSTANCE = 4;
	
	/**
	 * Constant representing an identifier type.
	 * A type with this type can be safely cast to <code>IIdentifierType</code>. 
	 */
	int TYPE_IDENTIFIER = 5;
	
	/**
	 * Constant representing a delegate type.
	 * A type with this type can be safely cast to <code>IDelegateType</code>. 
	 */
	int TYPE_DELEGATE = 6;
	
	/**
	 * Constant representing a point to function type.
	 * A type with this type can be safely cast to <code>IDelegateType</code>. 
	 */
	int TYPE_POINTER_TO_FUNCTION = 7;
	
	/**
	 * Constant representing a typeof type.
	 * A type with this type can be safely cast to <code>ITypeofType</code>. 
	 */
	int TYPE_TYPEOF = 8;
	
	/**
	 * Returns the type of this type. Check the constants declared
	 * in this interface.
	 */
	int getTypeType();

}
