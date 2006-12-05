package descent.core.dom;

/**
 * An invariant declaration:
 * 
 * <pre>
 * invariant { }
 * </pre>
 */
public interface IInvariantDeclaration extends IDeclaration {
	
	/**
	 * Returns the statement of the invariant.
	 */
	IStatement getBody();

}
