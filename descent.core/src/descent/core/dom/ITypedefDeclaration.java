package descent.core.dom;

import java.util.List;

import descent.internal.core.dom.TypedefDeclarationFragment;

/**
 * A typedef declaration:
 * 
 * <pre>
 * typedef type name = initializer;
 * </pre>
 * 
 * where initializer is optional.
 */
public interface ITypedefDeclaration extends IDeclaration {
	
	/**
	 * Returns the name of the typedef.
	 */
	List<TypedefDeclarationFragment> fragments();
	
	/**
	 * Returns the type maked a typedef.
	 */
	IType getType();

}
