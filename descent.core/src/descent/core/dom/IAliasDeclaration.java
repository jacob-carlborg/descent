package descent.core.dom;

/**
 * An alias declaration:
 * 
 * <pre>
 * alias type name;
 * </pre>
 */
public interface IAliasDeclaration extends IDeclaration, IModifiersContainer {
	
	/**
	 * The name of the alias.
	 */
	ISimpleName getName();
	
	/**
	 * The type this alias represents.
	 */
	IType getType();

}
