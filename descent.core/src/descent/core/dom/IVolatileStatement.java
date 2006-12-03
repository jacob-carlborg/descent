package descent.core.dom;

/**
 * A volatile statement:
 * 
 * <pre>
 * volatile statement
 * </pre>
 */
public interface IVolatileStatement extends IStatement {
	
	/**
	 * Returns the statement that is volatile.
	 */
	IStatement getBody();

}
