package descent.core.dom;

/**
 * A catch clause:
 * 
 * <pre>
 * catch([type name]) { (handler) }
 * </pre>
 */
public interface ICatchClause extends IElement {
	
	/**
	 * Returns the type to catch. May be <code>null</code>.
	 */
	IType getType();
	
	/**
	 * Returns the name assigned to the type to catch. May be <code>null</code>. 
	 */
	ISimpleName getName();
	
	/**
	 * Returns the statement to handle the exception.
	 */
	IStatement getHandler();

}
