package descent.core.dom;

/**
 * A scope statement is a statement that introduces
 * a new scope.
 */
public interface IScopeStatement extends IDescentStatement {
	
	/**
	 * Returns the statement in this scope.
	 */
	IDescentStatement getStatement();

}
