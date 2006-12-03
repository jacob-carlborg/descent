package descent.core.dom;

/**
 * A conditional statement may be a debug, version or static if.
 */
public interface IConditionalStatement extends IStatement {
	
	/**
	 * Returns the statement to execute in the "then" part.
	 */
	IStatement getBody();
	
	/**
	 * Returns the statement to execute in the "else" part, if any,
	 * or <code>null</code>.
	 */
	IStatement getElseBody();

}
