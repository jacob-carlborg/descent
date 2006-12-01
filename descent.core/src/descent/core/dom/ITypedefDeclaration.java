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
public interface ITypedefDeclaration extends IDeclaration, IModifiersContainer {
	
	/**
	 * Returns the name of the typedef.
	 */
	ISimpleName getName();
	
	/**
	 * Returns the type maked a typedef.
	 */
	IType getType();
	
	/**
	 * Returns the initializer, if any, or null.
	 */
	IInitializer getInitializer();

}
