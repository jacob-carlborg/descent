package descent.core.dom;

/**
 * An import declaration. An import declaration is breaked in imports.
 */
public interface IImportDeclaration extends IDElement, IModifiersContainer {
	
	/**
	 * Returns the imports of this declaration.
	 */
	IImport[] getImports();

}
