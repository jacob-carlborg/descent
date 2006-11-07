package descent.core.dom;

/**
 * Returns a base class for a class or interface declaration.
 */
public interface IBaseClass extends IDElement {
	
	/**
	 * Returns the type of the base class.
	 */
	IType getType();
	
	/**
	 * Returns the modifiers defined in the base class.
	 * @see IModifier
	 */
	int getModifiers();

}
