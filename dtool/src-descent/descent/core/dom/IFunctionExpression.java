package descent.core.dom;

/**
 * 
 * @author Ary
 *
 */
public interface IFunctionExpression extends IExpression {
	
	/**
	 * Returns the arguments of the function.
	 */
	IArgument[] getArguments();

	/**
	 * Determines whether this function is variadic.
	 */
	boolean isVariadic();
	
	/**
	 * Returns the body of the function.
	 */
	IStatement getBody();
	
	/**
	 * Returns the precondition of the function, if any, or <code>null</code>.
	 */
	IStatement getIn();
	
	/**
	 * Returns the postcondition of the function, if any, or <code>null</code>.
	 */
	IStatement getOut();
	
	/**
	 * Returns the name of the out clase of the function, if any, or <code>null</code>.
	 */
	IName getOutName();

}
