package dtool.refmodel;

import java.util.Iterator;

import descent.core.domX.ASTNode;

/**
 * Interface for a container node that contains nodes that are part of the same
 * scope as the container. 
 * (version/debug declarations, attribute declarations, etc.)
 */
public interface INonScopedBlock {
	
	Iterator<? extends ASTNode> getMembersIterator();
}
