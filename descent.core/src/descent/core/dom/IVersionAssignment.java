package descent.core.dom;

import descent.internal.core.dom.Version;

/**
 * Represents a conditional debug or version assignment.
 */
public interface IVersionAssignment extends IDeclaration {
	
	/**
	 * The value to assign.
	 */
	Version getVersion();

}
