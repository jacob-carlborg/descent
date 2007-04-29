package util.tree;

import util.AssertIn;
import dtool.dom.ast.ASTNode;

/**
 * Generic type for a tree node. The erasure TreeNode represents a homogenous tree
 * node, whereas a parameterized NODE represents the base node of a 
 * heterogenous tree. 
 *
 * @param <NODE> the base node for a heterogenous tree. 
 * @param <VISITOR> a visitor for that heterogenous tree.
 * @see ASTNode
 *
 * @author BrunoM
  */
public abstract class TreeNode<NODE extends TreeNode<NODE,VISITOR>, VISITOR extends ITreeVisitor<NODE>>
		implements ITreeNode<NODE, VISITOR> {

	/** AST node parent, null if the node is the tree root. */
	public NODE parent = null;
	
	/** {@inheritDoc} */
	public NODE getParent() {
		return parent;
	}
	
	/** {@inheritDoc} */
	public void setParent(NODE parent) {
		this.parent = parent;
	}
	
	/** {@inheritDoc} */
	public abstract NODE[] getChildren();
	
	/** {@inheritDoc} */
	public boolean hasChildren() {
		return getChildren().length > 0;
	}



	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	public void accept(VISITOR visitor) {
		AssertIn.isNotNull(visitor);

		// begin with the generic pre-visit
		visitor.preVisit((NODE)this);
		// dynamic dispatch to internal method for type-specific visit/endVisit
		this.accept0(visitor);
		// end with the generic post-visit
		visitor.postVisit((NODE)this);
	}
	
	/** Accepts the given visitor on a type-specific visit of the current node.
	 * <p>
	 * General template for implementation on each concrete element class:
	 * <pre> <code>
	 * boolean visitChildren = visitor.visit(this);
	 * if (visitChildren) {
	 *    // visit children in normal left to right reading order
	 *    acceptChild(visitor, getProperty1());
	 *    acceptChildren(visitor, rawListProperty);
	 *    acceptChild(visitor, getProperty2());
	 * }
	 * visitor.endVisit(this);
	 * </code> </pre>
	 * </p>
	 */
	protected abstract void accept0(VISITOR visitor);
	
	/** Gets the node's classname striped of package qualifier. */
	public final String toStringClassName() {
		String str = this.getClass().getName();
		int lastIx = str.lastIndexOf('.');
		return str.substring(lastIx+1);
	}

	/** Gets a string representation of the node. */
	public String toString() {
		return toStringClassName();
	}
}
