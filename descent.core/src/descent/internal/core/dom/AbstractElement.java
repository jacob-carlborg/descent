package descent.internal.core.dom;

import java.util.List;

import descent.core.dom.ElementVisitor;
import descent.core.dom.IDeclaration;
import descent.core.dom.IElement;

public abstract class AbstractElement implements IElement {
	
	public final static IElement[] NO_ELEMENTS = new IElement[0];
	public final static IDeclaration[] NO_DECLARATIONS = new IDeclaration[0];
	
	public String comments;
	public int start;
	public int length;
	public int modifiers;
	
	public int getStartPosition() {
		return start;
	}
	
	public int getLength() {
		return length;
	}
	
	public int getModifiers() {
		return modifiers;
	}
	
	public void addComment(String string, int blockCommentStart) {
		comments = string;
		if (blockCommentStart != -1) {
			this.length += this.start - blockCommentStart; 
			this.start = blockCommentStart;
		}
	}
	
	public String getComments() {
		return comments;
	}
	
	public final void accept(ElementVisitor visitor) {
		if (visitor == null) {
			throw new IllegalArgumentException();
		}
		// begin with the generic pre-visit
		visitor.preVisit(this);
		// dynamic dispatch to internal method for type-specific visit/endVisit
		accept0(visitor);
		// end with the generic post-visit
		visitor.postVisit(this);
	}
	
	/**
	 * Accepts the given visitor on a type-specific visit of the current node.
	 * This method must be implemented in all concrete AST node types.
	 * <p>
	 * General template for implementation on each concrete IElement class:
	 * <pre>
	 * <code>
	 * boolean visitChildren = visitor.visit(this);
	 * if (visitChildren) {
	 *    // visit children in normal left to right reading order
	 *    acceptChild(visitor, getProperty1());
	 *    acceptChildren(visitor, rawListProperty);
	 *    acceptChild(visitor, getProperty2());
	 * }
	 * visitor.endVisit(this);
	 * </code>
	 * </pre>
	 * Note that the caller (<code>accept</code>) take cares of invoking
	 * <code>visitor.preVisit(this)</code> and <code>visitor.postVisit(this)</code>.
	 * </p>
	 * 
	 * @param visitor the visitor object
	 */
	abstract void accept0(ElementVisitor visitor);
	
	/**
	 * Accepts the visitor on the child. If child is null,
	 * nothing happens.
	 */
	protected void acceptChild(ElementVisitor visitor, IElement child) {
		if (child == null)
			return;
		
		child.accept(visitor);
	}
	
	/**
	 * Accepts the visitor on the children. If children is null,
	 * nothing happens.
	 */
	protected void acceptChildren(ElementVisitor visitor, IElement[] children) {
		if (children == null)
			return;
		
		for(int i = 0; i < children.length; i++) {
			if (children[i] instanceof IElement) {
				acceptChild(visitor, children[i]);
			}
		}
	}
	
	/**
	 * Accepts the visitor on the children. If children is null,
	 * nothing happens.
	 */
	protected void acceptChildren(ElementVisitor visitor, List<? extends IElement> children) {
		if (children == null)
			return;
		
		for(int i = 0; i < children.size(); i++) {
			if (children.get(i) instanceof IElement) {
				acceptChild(visitor, children.get(i));
			}
		}
	}

}
