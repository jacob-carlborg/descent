package descent.core.dom;

import descent.internal.core.dom.Name;

/**
 * A module declaration:
 * 
 * <pre>
 * module foo.bar;
 * </pre>
 */
public interface IModuleDeclaration extends IDeclaration, ICommented {
	
	/**
	 * Returns the qualified name of the module.
	 */
	Name getName();

}
