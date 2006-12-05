package descent.core.dom;

import java.util.List;

import descent.internal.core.dom.EnumMember;

/**
 * An enum declaration.
 */
public interface IEnumDeclaration extends IDeclaration, ICommented {
	
	/**
	 * Returns the name of this enum. May be <code>null</code>
	 * if this enum is annonymous.
	 */
	ISimpleName getName();
	
	/**
	 * Returns the base type of this enum. May be <code>null</code>.
	 */
	IType getBaseType();
	
	/**
	 * Returns the members defined in this enum.
	 */
	List<EnumMember> enumMembers();

}
