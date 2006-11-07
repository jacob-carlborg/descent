package descent.core.dom;

/**
 * A protection declaration:
 * 
 * <pre>
 * prot {
 * }
 * </pre>
 * 
 * where "prot" is "public", for example.
 */
public interface IProtectionDeclaration extends IDElement {
	
	/**
	 * Returns the declaration definitions contained in this
	 * declaration.
	 */
	IDElement[] getDeclarationDefinitions();
	
	/**
	 * Returns the protection level.
	 * @see IModifier
	 */
	int getModifiers();

}
