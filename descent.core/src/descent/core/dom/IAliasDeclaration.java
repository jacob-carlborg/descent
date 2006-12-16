package descent.core.dom;

import java.util.List;

import descent.internal.core.dom.AliasDeclarationFragment;

/**
 * An alias declaration:
 * 
 * <pre>
 * alias type name;
 * </pre>
 */
public interface IAliasDeclaration extends IDeclaration, IModifiersContainer {
	
	/**
	 * The name of the alias.
	 */
	List<AliasDeclarationFragment> fragments();
	
	/**
	 * The type this alias represents.
	 */
	IType getType();

}
