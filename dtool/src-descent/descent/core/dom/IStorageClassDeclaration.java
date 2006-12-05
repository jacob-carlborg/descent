package descent.core.dom;

import descent.internal.core.dom.Declaration;

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
public interface IStorageClassDeclaration extends IDeclaration {
	
	/**
	 * Returns the declaration definitions contained in this declaration.
	 */
	IDeclaration[] getDeclarationDefinitions();

}
