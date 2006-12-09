package descent.core.dom;

import java.util.List;

import descent.internal.core.dom.Declaration;

/**
 * A conditional declaration may be a debug, version or static if.
 */
public interface IConditionalDeclaration extends IDeclaration {
	
	/**
	 * Returns the declarations on the "if" part of this
	 * declaration.
	 */
	List<Declaration> thenDeclarations();
	
	/**
	 * Returns the declarations on the "else" part of this
	 * declaration.
	 */
	List<Declaration> elseDeclarations();

}
