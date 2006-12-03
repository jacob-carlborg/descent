package descent.core.dom;

import java.util.List;

import descent.internal.core.dom.Statement;

/**
 * A compound statement.
 */
public interface ICompoundStatement extends IStatement {
	
	/**
	 * Returns the statements that make this compound statement.
	 */
	List<Statement> statements();

}
