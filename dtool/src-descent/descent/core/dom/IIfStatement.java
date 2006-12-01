package descent.core.dom;

/**
 * An if statement.
 * 
 * <pre>
 * if (arg = condition) {
 * } else {
 * }
 * </pre>
 * 
 * TODO: argument?
 */
public interface IIfStatement extends IStatement {
	
	/**
	 * Returns the argument, if any, of the if condition.
	 */
	IArgument getArgument();
	
	/**
	 * Returns the condition.
	 */
	IExpression getCondition();
	
	/**
	 * Returns the "then" statement.
	 */
	IStatement getThen();
	
	/**
	 * Returns the "else" statement, if any, or <code>null</code>.
	 */
	IStatement getElse();

}
