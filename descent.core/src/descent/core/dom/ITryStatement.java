package descent.core.dom;

/**
 * A try statement:
 * 
 * <pre>
 * try {
 * 
 * } catch (...) {
 * 
 * ...
 * 
 * } catch {
 * 
 * } finally {
 * 
 * }
 * </pre>
 */
public interface ITryStatement extends IStatement {
	
	/**
	 * Returns the statement to try.
	 */
	IStatement getTry();
	
	/**
	 * Returns the catches of the try statement.
	 */
	ICatch[] getCatches();
	
	/**
	 * Returns the finally statement, if any, or null.
	 */
	IStatement getFinally();

}
