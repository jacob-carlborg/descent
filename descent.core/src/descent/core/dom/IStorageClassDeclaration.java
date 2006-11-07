package descent.core.dom;

/**
 * A storage class declaration:
 * 
 * <pre>
 * storage {
 * }
 * </pre>
 * 
 * where storage is "static", for example.
 */
public interface IStorageClassDeclaration extends IDElement {
	
	/**
	 * Returns the declaration definitions contained in this declaration.
	 */
	IDElement[] getDeclarationDefinitions();

}
