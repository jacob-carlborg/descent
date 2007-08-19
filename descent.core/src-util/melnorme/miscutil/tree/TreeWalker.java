package melnorme.miscutil.tree;

/**
 * Generic tree walker that can walk any TreeNode. Uses TreeNode.getChildren();
 */
public abstract class TreeWalker {

	/** Traverses the node. */
	public final void traverse(IElement node) {
		if(enterNode(node)) {
			traverseChildren(node);
		}
		leaveNode(node);
	}
	
	private final void traverseChildren(IElement node) {
		for(IElement child : node.getChildren())
			traverse(child);
	}

	/** Performs the specific work on this node, on entry. */
	protected abstract boolean enterNode(IElement node);

	/** Performs the specific work on this node, on exit. */
	protected abstract void leaveNode(IElement node);
}