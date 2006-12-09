package descent.core.dom;

import descent.internal.core.dom.Version;

/**
 * A debug conditional declaration.
 */
public interface IDebugDeclaration extends IConditionalDeclaration {

	/**
	 * Returns the debug lever or identifier.
	 */
	Version getVersion();

}