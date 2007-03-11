package util.tree;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import util.AssertIn;
import dtool.dom.base.ASTNode;

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
public abstract class TreeNode<NODE extends TreeNode<NODE,VISITOR>, VISITOR extends TreeVisitor<NODE>>
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


	private static Pattern pattern = Pattern.compile("[A-Za-z0-9\\$]*$");

	/** Gets the node's classname striped of package qualifier. */
	public final String toStringClassName() {
		//String name = this.getClass().getName().replaceAll("^.*dom\\.base\\.", "");
		// XXX: This is a temporary solution to strip the package
		Matcher matcher = pattern.matcher(this.getClass().getName());
		matcher.find();
		return matcher.group();
	}

	/** Gets a string representation of the node. */
	public String toString() {
		return toStringClassName();
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
}
