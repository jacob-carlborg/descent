package descent.core.dom;

/**
 * An import present in an import declaration:
 * 
 * <pre>
 * ble = foo.bar : some = bla
 * --------------------------
 *        ^        ----------
 *        |             ^
 *     import           |
 *               selective import
 * </pre>
 */
public interface IImport extends IElement, IModifiersContainer {
	
	/**
	 * Returns the qualified name to import.
	 */
	IQualifiedName getQualifiedName();
	
	/**
	 * Returns the alias of the import, if any, or <code>null</code>.
	 */
	IName getAlias();
	
	/**
	 * Returns the selective imports to import from the qualified name. May
	 * be empty if no selective imports are defined.
	 */
	ISelectiveImport[] getSelectiveImports();

}
