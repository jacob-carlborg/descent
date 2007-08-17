package melnorme.miscutil.tree;

/**
 * Generic tree walker that can walk any TreeNode. Uses TreeNode.getChildren();
 */
public abstract class TreeWalker {

	/** Traverses the node. */
	public final void traverse(ITreeNode node) {
		if(enterNode(node)) {
			traverseChildren(node);
		}
		leaveNode(node);
	}
	
	private final void traverseChildren(ITreeNode node) {
		for(ITreeNode child : node.getChildren())
			traverse(child);
	}

	/** Performs the specific work on this node, on entry. */
	protected abstract boolean enterNode(ITreeNode node);

	/** Performs the specific work on this node, on exit. */
	protected abstract void leaveNode(ITreeNode node);
}