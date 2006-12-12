package descent.core.dom;

import java.util.List;

import descent.internal.core.dom.Declaration;

/**
 * A protection declaration:
 * 
 * <pre>
 * prot {
 * }
 * </pre>
 * 
 * where "prot" is "public", for example.
 */
public interface IProtectionDeclaration extends IDeclaration {
	
	/**
	 * Returns the declaration definitions contained in this
	 * declaration.
	 */
	List<Declaration> declarations();
	
	/**
	 * Returns the protection level.
	 * @see IModifier
	 */
	int getModifierFlags();

}
