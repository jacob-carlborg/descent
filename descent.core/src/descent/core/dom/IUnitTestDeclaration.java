package descent.core.dom;

/**
 * A unittest declaration:
 * 
 * <pre>
 * unittest {
 * 
 * }
 * </pre>
 */
public interface IUnitTestDeclaration extends IDeclaration {
	
	/**
	 * Returns the statement present in the unittest.
	 */
	IStatement getBody();

}
