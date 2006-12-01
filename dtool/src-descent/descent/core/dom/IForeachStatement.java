package descent.core.dom;

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
	IArgument[] getArguments();
	
	/**
	 * Returns the iterable expression.
	 */
	IExpression getIterable();
	
	/**
	 * Returns the body of this foreach.
	 */
	IStatement getBody();
	
	/**
	 * Determines if this is a foreach_revere actualy.
	 */
	boolean isReverse();

}
