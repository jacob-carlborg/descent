package descent.core.dom;

/**
 * A typedef declaration:
 * 
 * <pre>
 * typedef type name = initializer;
 * </pre>
 * 
 * where initializer is optional.
 */
public interface ITypedefDeclaration extends IDElement, IModifiersContainer {
	
	/**
	 * Returns the name of the typedef.
	 */
	IName getName();
	
	/**
	 * Returns the type maked a typedef.
	 */
	IType getType();
	
	/**
	 * Returns the initializer, if any, or null.
	 */
	IInitializer getInitializer();

}
