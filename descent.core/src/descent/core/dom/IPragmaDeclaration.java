package descent.core.dom;

/**
 * A pragma declaration:
 * 
 * <pre>
 * pragma(name, arg1, arg2, ..., argN) { }
 * </pre>
 */
public interface IPragmaDeclaration extends IDeclaration {
	
	/**
	 * Returns the name of the pragma.
	 */
	ISimpleName getIdentifier();
	
	/**
	 * Returns the arguments of the pragma.
	 */
	IExpression[] getArguments();
	
	/**
	 * Returns the declaration definitions contained in this
	 * pragma declaration.
	 */
	IDeclaration[] getDeclarationDefinitions();

}
