package descent.core.dom;

/**
 * A label statement:
 * 
 * <pre>
 * label: statement
 * </pre>
 */
public interface ILabelStatement extends IDescentStatement {
	
	/**
	 * Returns the name of the label.
	 */
	IName getName();
	
	/**
	 * Returns the statement.
	 */
	IDescentStatement getStatement();

}
