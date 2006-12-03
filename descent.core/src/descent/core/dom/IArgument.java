package descent.core.dom;

import descent.internal.core.dom.Argument;

/**
 * An argument to a function or something else:
 * 
 * <pre>
 * [ | in | out | inout | lazy ] type name [ = defaultValue ]
 * </pre>
 */
public interface IArgument extends IElement {
	
	/**
	 * Returns the name of this argument.
	 */
	ISimpleName getName();
	
	/**
	 * Returns the type of this argument.
	 */
	IType getType();
	
	/**
	 * Returns the default value of this argument, if any,
	 * or <code>null</code>.
	 */
	IExpression getDefaultValue();
	
	/**
	 * Returns the passage mode.
	 */
	Argument.PassageMode getPassageMode();

}
