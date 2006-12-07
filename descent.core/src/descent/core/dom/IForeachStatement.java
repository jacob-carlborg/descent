package descent.core.dom;

import java.util.List;

import descent.internal.core.dom.Argument;

/**
 * A foreach statement:
 * 
 * <pre>
 * foreach(arg1, arg2, ..., argN ; iterable) {
 *     body
 * }
 * </pre>
 */
public interface IForeachStatement extends IStatement {
	
	/**
	 * Returns the arguments.
	 */
	List<Argument> arguments();
	
	/**
	 * Returns the iterable expression.
	 */
	IExpression getExpression();
	
	/**
	 * Returns the body of this foreach.
	 */
	IStatement getBody();
	
	/**
	 * Determines if this is a foreach_revere actualy.
	 */
	boolean isReverse();

}
