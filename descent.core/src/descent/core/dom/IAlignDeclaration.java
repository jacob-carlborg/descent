package descent.core.dom;

import java.util.List;

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
	int getAlign();
	
	/**
	 * Returns the declaration definitions contained in this declaration.
	 */
	List<Declaration> declarations();

}
