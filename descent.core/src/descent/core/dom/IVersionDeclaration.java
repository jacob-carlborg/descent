package descent.core.dom;

/**
 * A version conditional declaration.
 */
public interface IVersionDeclaration extends IConditionalDeclaration {
	
	/**
	 * Returns the version identifier or number.
	 */
	ISimpleName getVersion();	

}
