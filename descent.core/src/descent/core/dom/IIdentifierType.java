package descent.core.dom;

import descent.internal.core.dom.SimpleName;

/**
 * An identifier type.
 */
public interface IIdentifierType extends IType {
	
	/**
	 * Returns the short name of the type. For example
	 * if the type is "foo.Bar", then "Bar" is returned.
	 */
	SimpleName getName();

}
