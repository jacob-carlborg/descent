package descent.core.dom;

/**
 * An enum declaration.
 */
public interface IEnumDeclaration extends IDeclaration {
	
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
	IEnumMember[] getMembers();

}
