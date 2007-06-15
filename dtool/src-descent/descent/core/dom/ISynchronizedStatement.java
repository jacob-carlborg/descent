package descent.core.dom;

/**
 * A synchronized statement:
 * 
 * <pre>
 * synchronized(expr) {
 * 
 * }
 * </pre>
 * 
 * where "(expr)" is optional. 
 */
public interface ISynchronizedStatement extends IDescentStatement {
	
	/**
	 * Returns the expression to use as a lock, or null.
	 */
	IExpression getExpression();
	
	/**
	 * Returns the statement to synchronize.
	 */
	IDescentStatement getStatement();

}
