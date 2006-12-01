package descent.core.dom;

/**
 * A label statement:
 * 
 * <pre>
 * label: statement
 * </pre>
 */
public interface ILabelStatement extends IStatement {
	
	/**
	 * Returns the name of the label.
	 */
	ISimpleName getName();
	
	/**
	 * Returns the statement.
	 */
	IStatement getStatement();

}
