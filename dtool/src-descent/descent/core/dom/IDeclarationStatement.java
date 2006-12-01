package descent.core.dom;

/**
 * A declaration statement.
 */
public interface IDeclarationStatement extends IStatement {
	
	/**
	 * Returns the declaration.
	 */
	IDeclaration getDeclaration();

}
