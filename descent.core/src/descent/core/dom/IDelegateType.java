package descent.core.dom;

import java.util.List;

import descent.internal.core.dom.Argument;

/**
 * A delegate or pointer to function type.
 */
public interface IDelegateType extends IType {
	
	/**
	 * Returns the return type of the function pointed by this delegate.
	 */
	IType getReturnType();

	/**
	 * Returns the arguments of the function pointed by this delegate.
	 */
	List<Argument> arguments();

}
