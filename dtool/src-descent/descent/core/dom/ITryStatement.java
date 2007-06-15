package descent.core.dom;

import descent.internal.core.dom.Catch;

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
public interface ITryStatement extends IDescentStatement {
	
	/**
	 * Returns the statement to try.
	 */
	IDescentStatement getTry();
	
	/**
	 * Returns the catches of the try statement.
	 */
	Catch[] getCatches();
	
	/**
	 * Returns the finally statement, if any, or null.
	 */
	IDescentStatement getFinally();

}
