package descent.launching.model;

import org.eclipse.debug.core.model.IVariable;

/**
 * A variable that can have children.
 * 
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 */
public interface IParentVariable extends IDescentVariable {
	
	/**
	 * Adds a child to this variable.
	 * @param variable the variable to add
	 */
	void addChild(IVariable variable);
	
	/**
	 * Adds many child to this variable.
	 * @param variables the variables to add
	 */
	void addChildren(IVariable[] variables);

}
