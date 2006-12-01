package descent.core.dom;

/**
 * A conditional declaration may be a debug, version or static if.
 */
public interface IConditionalDeclaration extends IDeclaration {
	
	/**
	 * Returns the declarations on the "if" part of this
	 * declaration.
	 */
	IDeclaration[] getIfTrueDeclarationDefinitions();
	
	/**
	 * Returns the declarations on the "else" part of this
	 * declaration.
	 */
	IDeclaration[] getIfFalseDeclarationDefinitions();

}
