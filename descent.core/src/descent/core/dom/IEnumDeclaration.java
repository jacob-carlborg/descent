package descent.core.dom;

/**
 * An enum declaration.
 */
public interface IEnumDeclaration extends IDElement {
	
	/**
	 * Returns the name of this enum. May be <code>null</code>
	 * if this enum is annonymous.
	 */
	IName getName();
	
	/**
	 * Returns the base type of this enum. May be <code>null</code>.
	 */
	IType getBaseType();
	
	/**
	 * Returns the members defined in this enum.
	 */
	IEnumMember[] getMembers();

}
