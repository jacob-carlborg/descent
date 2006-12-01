package descent.core.dom;

/**
 * A compound statement.
 */
public interface ICompoundStatement extends IStatement {
	
	/**
	 * Returns the statements that make this compound statement.
	 */
	IStatement[] getStatements();

}
