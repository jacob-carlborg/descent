package descent.core.dom;

import java.util.List;

import descent.internal.core.dom.Declaration;
import descent.internal.core.dom.Modifier;

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
public interface IModifierDeclaration extends IDeclaration {
	
	/**
	 * Returns the declaration definitions contained in this
	 * declaration.
	 */
	List<Declaration> declarations();
	
	/**
	 * Returns the protection level.
	 * @see IModifier
	 */
	Modifier getModifier();

}
