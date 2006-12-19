package descent.core.dom;

import descent.internal.core.dom.Modifier;

/**
 * Returns a base class for a class or interface declaration.
 */
public interface IBaseClass extends IElement {
	
	/**
	 * Returns the type of the base class.
	 */
	IType getType();
	
	/**
	 * Returns the modifiers defined in the base class.
	 * @see IModifier
	 */
	Modifier getModifier();

}
