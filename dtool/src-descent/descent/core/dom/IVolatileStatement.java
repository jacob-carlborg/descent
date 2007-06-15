package descent.core.dom;

/**
 * A volatile statement:
 * 
 * <pre>
 * volatile statement
 * </pre>
 */
public interface IVolatileStatement extends IDescentStatement {
	
	/**
	 * Returns the statement that is volatile.
	 */
	IDescentStatement getStatement();

}
