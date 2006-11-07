package descent.core.dom;

/**
 * A default statement:
 * 
 * <pre>
 * default: statement
 * </pre>
 */
public interface IDefaultStatement extends IStatement {
	
	/**
	 * Returns the statement.
	 */
	IStatement getStatement();	

}
