package descent.core.dom;

/**
 * A compound statement.
 */
public interface ICompoundStatement extends IDescentStatement {
	
	/**
	 * Returns the statements that make this compound statement.
	 */
	IDescentStatement[] getStatements();

}
