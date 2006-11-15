package descent.internal.core.dom;

import java.util.List;

import descent.core.dom.IDElement;
import descent.core.dom.IDElementVisitor;

public abstract class AbstractElement implements IDElement {
	
	public final static IDElement[] NO_ELEMENTS = new IDElement[0];
	
	public String comments;
	public int start;
	public int length;
	public int modifiers;
	
	public void addComment(String string, int blockCommentStart) {
		comments = string;
		if (blockCommentStart != -1) {
			this.length += this.start - blockCommentStart; 
			this.start = blockCommentStart;
		}
	}
	
	public int getOffset() {
		return start;
	}
	
	public int getLength() {
		return length;
	}
	
	public String getComments() {
		return comments;
	}
	
	public int getModifiers() {
		return modifiers;
	}
	
	/**
	 * Accepts the visitor on the child. If child is null,
	 * nothing happens.
	 */
	protected void acceptChild(IDElementVisitor visitor, IDElement child) {
		if (child == null)
			return;
		
		child.accept(visitor);
	}
	
	/**
	 * Accepts the visitor on the children. If children is null,
	 * nothing happens.
	 */
	protected void acceptChildren(IDElementVisitor visitor, IDElement[] children) {
		if (children == null)
			return;
		
		for(int i = 0; i < children.length; i++) {
			if (children[i] instanceof IDElement) {
				acceptChild(visitor, children[i]);
			}
		}
	}
	
	/**
	 * Accepts the visitor on the children. If children is null,
	 * nothing happens.
	 */
	protected void acceptChildren(IDElementVisitor visitor, List<? extends IDElement> children) {
		if (children == null)
			return;
		
		for(int i = 0; i < children.size(); i++) {
			if (children.get(i) instanceof IDElement) {
				acceptChild(visitor, children.get(i));
			}
		}
	}

}
