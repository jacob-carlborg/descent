package descent.core.dom;

/**
 * A conditional declaration may be a debug, version or static if.
 */
public interface IConditionalDeclaration extends IDElement {
	
	/**
	 * Constant representing a debug declaration.
	 * A conditional declaration with this type can be safely cast to <code>IDebugDeclaration</code>. 
	 */
	int CONDITIONAL_DEBUG = 1;
	
	/**
	 * Constant representing a version declaration.
	 * A conditional declaration with this type can be safely cast to <code>IVersionDeclaration</code>. 
	 */
	int CONDITIONAL_VERSION = 2;
	
	/**
	 * Constant representing a static if declaration.
	 * A conditional declaration with this type can be safely cast to <code>IStaticIfDeclaration</code>. 
	 */
	int CONDITIONAL_STATIC_IF = 3;
	
	/**
	 * Constant representing the deprecated iftype declaration.
	 * A conditional declaration with this type can be safely cast to <code>IIftypeDeclaration</code>. 
	 */
	int CONDITIONAL_IFTYPE = 4;
	
	/**
	 * Returns whether this declaration is debug, version or
	 * static if. Check the constants defined in this interface.
	 */
	int getConditionalDeclarationType();
	
	/**
	 * Returns the declarations on the "if" part of this
	 * declaration.
	 */
	IDElement[] getIfTrueDeclarationDefinitions();
	
	/**
	 * Returns the declarations on the "else" part of this
	 * declaration.
	 */
	IDElement[] getIfFalseDeclarationDefinitions();

}
