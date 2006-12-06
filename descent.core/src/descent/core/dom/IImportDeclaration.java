package descent.core.dom;

import java.util.List;

import descent.internal.core.dom.Import;

/**
 * An import declaration. An import declaration is breaked in imports.
 */
public interface IImportDeclaration extends IDeclaration, IModifiersContainer {
	
	/**
	 * Returns the imports of this declaration.
	 */
	List<Import> imports();
	
	/**
	 * Determines if this import declaration is static.
	 */
	boolean isStatic();

}
