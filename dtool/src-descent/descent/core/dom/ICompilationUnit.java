package descent.core.dom;

/**
 * Represents an entire source file.
 */
public interface ICompilationUnit extends IElement {
	
	/**
	 * Returns the module declaration in this source file, if any,
	 * or <code>null</code>.
	 */
	IModuleDeclaration getModuleDeclaration();
	
	/**
	 * Returns the declarations present in this compilation unit.
	 */
	IDeclaration[] getDeclarationDefinitions();
	
	/**
	 * Returns problems detected while parsing this source file.
	 */
	IProblem[] getProblems();

}