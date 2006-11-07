package descent.core.dom;

/**
 * A module declaration:
 * 
 * <pre>
 * module foo.bar;
 * </pre>
 */
public interface IModuleDeclaration extends IDElement, ICommented {
	
	/**
	 * Returns the qualified name of the module.
	 */
	IQualifiedName getQualifiedName();

}
