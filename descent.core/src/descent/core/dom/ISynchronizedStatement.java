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
public interface ISynchronizedStatement extends IStatement {
	
	/**
	 * Returns the expression to use as a lock, or null.
	 */
	IExpression getExpression();
	
	/**
	 * Returns the statement to synchronize.
	 */
	IStatement getBody();

}
