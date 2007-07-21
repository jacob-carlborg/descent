package descent.debug.core.model;

import org.eclipse.debug.core.model.IRegister;
import org.eclipse.debug.core.model.IRegisterGroup;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IVariable;

/**
 * <p>A debug element factory is provided to a {@link IDebugger} to create
 * the different objects it must return.</p>
 * 
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 */
public interface IDebugElementFactory {
	
	/**
	 * Creates a new register with the given name and value, in the given register group.
	 * @param registerGroup the register group to contain the register
	 * @param name the name of the register
	 * @param value the value of the register
	 * @return the new register
	 */
	IRegister newRegister(IRegisterGroup registerGroup, String name, String value);
	
	/**
	 * Creates a new variable in the given stack frame, with the given name and value. The
	 * variable may potentialy have children.
	 * @param stackFrame the number of stack frame
	 * @param name the name of the variable
	 * @param value the value of the variable
	 * @return the variable
	 */
	IParentVariable newParentVariable(int stackFrame, String name, String value);
	
	/**
	 * Creates a new variable that lazily requests its children. The children
	 * are request via the given expression. The value of the variable may
	 * be anything not null: its type, an empty string, etc.
	 * @param stackFrame the number of stack frame
	 * @param name the name of the variable
	 * @param value a convenient value
	 * @param expression the expression to evaluate to obtain the children
	 * @return the variable
	 */
	IVariable newLazyVariable(int stackFrame, String name, String value, String expression);
	
	/**
	 * Creates a new stack frame with the given name, number, source name and line number.
	 * @param name the name of the stack frame. This is generally the function where
	 * the stack frame is
	 * @param number a number
	 * @param sourceName the source name. May be null
	 * @param lineNumber the line number. -1 for unknow.
	 * @return a new stack frame
	 */
	IStackFrame newStackFrame(String name, int number, String sourceName, int lineNumber);

}
