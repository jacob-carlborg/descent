package descent.core.dom;

/**
 * A scope statement.
 * 
 * TODO: what is this?
 */
public interface IScopeStatement extends IStatement {
	
	/**
	 * Returns the statement in this scope.
	 */
	IStatement getStatement();

}
