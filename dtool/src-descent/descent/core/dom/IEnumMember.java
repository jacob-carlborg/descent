package descent.core.dom;

/**
 * A member of an enum.
 */
public interface IEnumMember extends IDescentElement {
	
	/**
	 * The name of this member.
	 */
	IName getName();
	
	/**
	 * The value declared for this member, or <code>null</code>.
	 */
	IExpression getValue();

}
