package descent.core.dom;

import java.util.List;

import descent.internal.core.dom.CatchClause;

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
	IStatement getBody();
	
	/**
	 * Returns the catches of the try statement.
	 */
	List<CatchClause> catchClauses();
	
	/**
	 * Returns the finally statement, if any, or null.
	 */
	IStatement getFinally();

}
