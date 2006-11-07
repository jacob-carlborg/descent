package descent.core.dom;

/**
 * A conditional statement may be a debug, version or static if.
 */
public interface IConditionalStatement extends IStatement {
	
	/**
	 * Constant representing a debug statement.
	 * A conditional statement with this type can be safely cast to <code>IDebugStatement</code>. 
	 */
	int CONDITIONAL_DEBUG = 1;
	
	/**
	 * Constant representing a version statement.
	 * A conditional statement with tis type can be safely cast to <code>IVersionStatement</code>. 
	 */
	int CONDITIONAL_VERSION = 2;
	
	/**
	 * Constant representing a static if statement.
	 * A conditional statement with this type can be safely cast to <code>IStaticIfStatement</code>. 
	 */
	int CONDITIONAL_STATIC_IF = 3;
	
	/**
	 * Returns the type of this conditional statement. Check the constants
	 * defined in this interface.
	 */
	int getConditionalStatementType();
	
	/**
	 * Returns the statement to execute in the "then" part.
	 */
	IStatement getThen();
	
	/**
	 * Returns the statement to execute in the "else" part, if any,
	 * or <code>null</code>.
	 */
	IStatement getElse();

}
