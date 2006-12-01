package descent.core.domX;

import java.util.List;

import descent.core.dom.IElement;

public abstract class ASTNode implements IElement {

	/**
	 * A character index into the original source string, 
	 * or <code>-1</code> if no source position information is available
	 * for this node; <code>-1</code> by default.
	 */
	public int startPos = -1;
	/**
	 * A character length, or <code>0</code> if no source position
	 * information is recorded for this node; <code>0</code> by default.
	 */
	public int length = 0;

	/**
	 * Sets the source range of the original source file where the source
	 * fragment corresponding to this node was found.
	 */
	public final void setSourceRange(int startPosition, int length) {
		if (startPosition >= 0 && length < 0) {
			throw new IllegalArgumentException();
		}
		if (startPosition < 0 && length != 0) {
			throw new IllegalArgumentException();
		}
		// source positions are not considered a structural property
		// but we protect them nevertheless
		//checkModifiable();
		this.startPos = startPosition;
		this.length = length;
	}
	
	public int getStartPos() {
		return startPos;
	}
	public int getOffset() {
		return startPos;
	}

	public int getLength() {
		return length;
	}

	public final String nodeToString() {
		String name = this.getClass().getName().replaceAll("^.*\\.dom\\.", "");
		return name + " [" + startPos+"+"+length+"]";
	}

	/**
	 * Accepts the given visitor on a visit of the current node.
	 * 
	 * @param visitor the visitor object
	 * @exception IllegalArgumentException if the visitor is null
	 */
	public void accept(ASTVisitor visitor) {
		if (visitor == null) {
			throw new IllegalArgumentException();
		}
		// begin with the generic pre-visit
		visitor.preVisit(this);
		// dynamic dispatch to internal method for type-specific visit/endVisit
		this.accept0(visitor);
		// end with the generic post-visit
		visitor.postVisit(this);
	}

	
	/**
	 * Accepts the given visitor on a type-specific visit of the current node.
	 * This method must be implemented in all concrete AST node types.
	 * <p>
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
	 * Note that the caller (<code>accept</code>) take cares of invoking
	 * <code>visitor.preVisit(this)</code> and <code>visitor.postVisit(this)</code>.
	 * </p>
	 */
	public abstract void accept0(ASTVisitor visitor);
	
	/**
	 * Accepts the given visitor on a visit of the current node.
	 */
	public final void acceptChild(ASTVisitor visitor, IElement child) {
		// FIXME: that IElement above is ASTNode
		if (child == null) {
			return;
		}
		child.accept(visitor);
	}

	/**
	 * Accepts the given visitor on a visit of the given list of
	 * child nodes. 
	 */
	public final void acceptChildren(ASTVisitor visitor, Object[] children) {
		// FIXME: that Object above is ASTNode
		if (children == null)
			return;
		
		for(int i = 0; i < children.length; i++) {
			if (children[i] instanceof ASTNode) {
				acceptChild(visitor, (ASTNode) children[i]);
			}
		}
	}
	
	
	/**
	 * Accepts the visitor on the children. If children is null,
	 * nothing happens.
	 */
	public void acceptChildren(ASTVisitor visitor, List<? extends Object> children) {
		// FIXME: that Object above is ASTNode
		if (children == null)
			return;
		
		for(int i = 0; i < children.size(); i++) {
			if (children.get(i) instanceof ASTNode) {
				acceptChild(visitor, (ASTNode) children.get(i));
			}
		}
	}

}