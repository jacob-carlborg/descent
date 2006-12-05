package descent.core.dom;

import descent.internal.core.dom.Declaration;

/**
 * A declaration statement.
 */
public interface IDeclarationStatement extends IStatement {
	
	/**
	 * Returns the declaration.
	 */
	Declaration getDeclaration();

}
