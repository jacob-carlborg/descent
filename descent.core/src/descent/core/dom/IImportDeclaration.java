package descent.core.dom;

/**
 * An import declaration. An import declaration is breaked in imports.
 */
public interface IImportDeclaration extends IDeclaration, IModifiersContainer {
	
	/**
	 * Returns the imports of this declaration.
	 */
	IImport[] getImports();
	
	/**
	 * Determines if this import declaration is static.
	 */
	boolean isStatic();

}
