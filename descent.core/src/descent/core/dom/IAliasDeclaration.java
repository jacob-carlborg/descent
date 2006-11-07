package descent.core.dom;

/**
 * An alias declaration:
 * 
 * <pre>
 * alias type name;
 * </pre>
 */
public interface IAliasDeclaration extends IDElement, IModifiersContainer {
	
	/**
	 * The name of the alias.
	 */
	IName getName();
	
	/**
	 * The type this alias represents.
	 */
	IType getType();

}
