package descent.core.dom;

import java.util.List;

import descent.internal.core.dom.VariableDeclarationFragment;

/**
 * A variable declaration.
 */
public interface IVariableDeclaration extends IDeclaration, ICommented {
	
	/**
	 * Returns the type of the variable. Note that multiple variables
	 * declared in the same declaration share the same <code>IType</code> instance.
	 */
	IType getType();
	
	/**
	 * Returns the name of the variable.
	 */
	List<VariableDeclarationFragment> fragments();

}
