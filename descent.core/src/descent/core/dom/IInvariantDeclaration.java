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
	 * Returns the "invariant" part.
	 */
	IName getName();
	
	/**
	 * Returns the statement of the invariant.
	 */
	IStatement getStatement();

}
