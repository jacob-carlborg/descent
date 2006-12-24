package descent.internal.core.parser;

import descent.core.dom.Expression;
import descent.core.dom.Type;

/**
 * Interface that encapsulates the basic functionality provided
 * by the old Type class found in the DMD package. We adapt
 * the new classes to this interface through {@link TypeAdapter}
 * to maintain the same funcionality.
 */
public interface IDmdType {
	
	/**
	 * Sets the "next" field of this type.
	 */
	void setNext(Type type);
	
	/**
	 * Returns the "next" field of this type.
	 */
	Type getNext();
	
	/**
	 * Returns the "ty" field.
	 */
	TY getTY();
	
	/**
	 * The "toExpression" method found in the old
	 * type.
	 */
	Expression toExpression();
	
	/**
	 * Returns the real type, if this one is an adapted type.
	 */
	Object getAdaptedType();

}