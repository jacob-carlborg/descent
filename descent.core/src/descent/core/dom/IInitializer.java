package descent.core.dom;

/**
 * An initializer.
 */
public interface IInitializer extends IDElement {
	
	/**
	 * Constant representing an expression intializer.
	 * A D element with this type can be safely cast to <code>IExpressionInitializer</code>. 
	 */
	int EXPRESSION_INITIALIZER = 1;
	
	// TODO
	int VOID_INITIALIZER = 2;
	
	/**
	 * Constant representing an array intializer.
	 * A D element with this type can be safely cast to <code>IArrayInitializer</code>. 
	 */
	int ARRAY_INITIALIZER = 3;
	
	/**
	 * Constant representing a struct intializer.
	 * A D element with this type can be safely cast to <code>IStructInitializer</code>. 
	 */
	int STRUCT_INITIALIZER = 4;
	
	/**
	 * The initializer type. Check the constants defined
	 * in this interface.
	 */
	int getInitializerType();

}
