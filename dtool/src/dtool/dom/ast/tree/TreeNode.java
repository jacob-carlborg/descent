package dtool.dom.ast.tree;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import util.AssertIn;
import dtool.dom.ast.CommonVisitor;
import dtool.dom.base.ASTNode;

/**
 * Generic type for a tree node. The erasure TreeNode represents a homogenous tree
 * node, whereas a parameterized TreeNode represents the base node of a 
 * heterogenous tree. 
 *
 * @param <NODE> the base node for a heterogenous tree. 
 * @param <VISITOR> a visitor for that heterogenous tree.
 * @see ASTNode
 *
 * @author BrunoM
  */
public abstract class TreeNode<NODE extends TreeNode, VISITOR extends CommonVisitor> {

	/** AST node parent, null if the node is the tree root. */
	public NODE parent = null;
	
	/** Returns the parent of this node, or <code>null</code> if none. */
	public NODE getParent() {
		return parent;
	}

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
	
	/** Returns the node's children, ordered. */
	public abstract NODE[] getChildren() ;
	
	
	/** Accepts the given visitor on a visit of the current node. */
	@SuppressWarnings("unchecked")
	public void accept(VISITOR visitor) {
		AssertIn.isNotNull(visitor);

		// begin with the generic pre-visit
		visitor.preVisit( this);
		// dynamic dispatch to internal method for type-specific visit/endVisit
		this.accept0(visitor);
		// end with the generic post-visit
		visitor.postVisit(this);
	}
	
	/** Accepts the given visitor on a type-specific visit of the current node.
	 * This method must be implemented in all concrete AST node types.
	 * <p>
	 * Note that the caller (<code>accept</code>) take cares of invoking
	 * <code>visitor.preVisit(this)</code> and <code>visitor.postVisit(this)</code>.
	 * </p><p>
	 * General template for implementation on each concrete IElement class:
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
	public abstract void accept0(VISITOR visitor);
	
	/** Accepts the visitor on child. If child is null, nothing happens. */
	public final void acceptChild(VISITOR visitor, TreeNode<NODE,VISITOR> child) {
		if (child != null) {
			child.accept(visitor);
		}
	}
	
	/** Same as {@link #acceptChild(CommonVisitor, TreeNode) } */
	public final void acceptChildren(VISITOR visitor, TreeNode<NODE,VISITOR> child) {
		acceptChild(visitor, child);
	}
	
	/** Accepts the visitor on the children. If children is null, nothing
	 * happens.
	 */
	public final void acceptChildren(VISITOR visitor, TreeNode<NODE,VISITOR>[] children) {
		if (children == null)
			return;
		
		for(int i = 0; i < children.length; i++) {
			//if (children[i] instanceof TreeNode) {
				acceptChild(visitor, children[i]);
			//}
		}
	}

	/** Accepts the visitor on the children. If children is null, nothing
	 * happens.
	 */
	public void acceptChildren(VISITOR visitor, List<? extends Object> children) {
		// FIXME: that Object above is NODE, must clean Old AST first.
		if (children == null)
			return;
		
		for(int i = 0; i < children.size(); i++) {
			//if (children.get(i) instanceof TreeNode) {
				acceptChild(visitor, (TreeNode<NODE,VISITOR>) children.get(i));
			//}
		}
	}

}