package descent.core.dom;

/**
 * An identifier type.
 */
public interface IIdentifierType extends IType {
	
	/**
	 * Returns the short name of the type. For example
	 * if the type is "foo.Bar", then "Bar" is returned.
	 */
	String getShortName();
	
	/**
	 * Returns the qualified name of this type.
	 */
	String toString();

}
