package descent.core.dom;

/**
 * An element that contains modifiers, such as "auto", "static", "private", etc.
 */
public interface IModifiersContainer {
	
	/**
	 * Returns the modifiers.
	 * @see IModifier
	 */
	int getModifierFlags();

}
