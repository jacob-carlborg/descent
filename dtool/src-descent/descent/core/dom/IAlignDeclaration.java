package descent.core.dom;

import descent.internal.core.dom.Declaration;

/**
 * An align declaration:
 * 
 * <pre>
 * align(n) { }
 * </pre>
 *
 */
public interface IAlignDeclaration extends IDeclaration {
	
	/**
	 * Returns the align.
	 */
	long getAlign();
	
	/**
	 * Returns the declaration definitions contained in this declaration.
	 */
	IDeclaration[] getDeclarationDefinitions();

}
