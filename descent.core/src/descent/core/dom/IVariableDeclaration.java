package descent.core.dom;

/**
 * A variable declaration.
 */
public interface IVariableDeclaration extends IDElement, IModifiersContainer, ICommented {
	
	/**
	 * Returns the type of the variable. Note that multiple variables
	 * declared in the same declaration share the same <code>IType</code> instance.
	 */
	IType getType();
	
	/**
	 * Returns the name of the variable.
	 */
	IName getName();
	
	/**
	 * Returns the initializer of the variable, if any, or null.
	 */
	IInitializer getInitializer();

}
