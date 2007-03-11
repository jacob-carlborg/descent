package util.tree;


public interface ITreeNode<NODE extends ITreeNode, VISITOR extends TreeVisitor> {

	/** Returns the parent of this node, or <code>null</code> if none. */
	public NODE getParent();

	/** Set the parent of this node, can be null. */
	public void setParent(NODE parent);
	
	/** Returns the node's children, ordered. */
	public NODE[] getChildren();

	/** Accepts the given visitor on a visit of the current node. */
	public void accept(VISITOR visitor);

}