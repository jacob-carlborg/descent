package descent.core.dom;

import descent.internal.core.dom.Modifier;

/**
 * An element that contains modifiers, such as "auto", "static", "private", etc.
 */
public interface IModifiersContainer {
	
	/**
	 * Returns the modifiers.
	 * @see IModifier
	 */
	Modifier getModifier();

}
