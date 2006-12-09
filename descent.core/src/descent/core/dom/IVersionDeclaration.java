package descent.core.dom;

import descent.internal.core.dom.Version;

/**
 * A version conditional declaration.
 */
public interface IVersionDeclaration extends IConditionalDeclaration {
	
	/**
	 * Returns the version identifier or number.
	 */
	Version getVersion();	

}
