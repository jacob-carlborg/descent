package util.tree;

/**
 * Interface for the node of a heterogeneous tree.
 */
public interface ITreeNodeGeneric<NODE extends ITreeNodeGeneric<NODE>>
		extends ITreeNode {

	/** {@inheritDoc} */
	public NODE getParent();

	/** Set the parent of this node. Can be null. */
	public void setParent(NODE parent);
	
	/** Returns the node's children, ordered. */
	public NODE[] getChildren();

}