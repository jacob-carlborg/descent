package util.tree;

/**
 * Interface for the node of a heterogenous tree.
 */
public interface ITreeNode<NODE extends ITreeNode, VISITOR extends TreeVisitor>
		extends IElement, IVisitable<VISITOR> {

	/** {@inheritDoc} */
	public NODE getParent();

	/** Set the parent of this node. Can be null. */
	public void setParent(NODE parent);
	
	/** Returns the node's children, ordered. */
	public NODE[] getChildren();

}