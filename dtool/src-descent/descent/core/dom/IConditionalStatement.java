package descent.core.dom;

/**
 * A conditional statement may be a debug, version or static if.
 */
public interface IConditionalStatement extends IDescentStatement {
	
	/**
	 * Returns the statement to execute in the "then" part.
	 */
	IDescentStatement getThen();
	
	/**
	 * Returns the statement to execute in the "else" part, if any,
	 * or <code>null</code>.
	 */
	IDescentStatement getElse();

}
