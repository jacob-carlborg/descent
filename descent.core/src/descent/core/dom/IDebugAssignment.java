package descent.core.dom;

import descent.internal.core.dom.Version;

/**
 * Represents a conditional debug or version assignment.
 */
public interface IDebugAssignment extends IDeclaration {
	
	/**
	 * The value to assign.
	 */
	Version getVersion();

}
