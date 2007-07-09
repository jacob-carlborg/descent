package dtool.dom.ast;

import melnorme.miscutil.Assert;
import melnorme.miscutil.AssertIn;
import melnorme.miscutil.tree.TreeNode;
import descent.core.dom.IDescentElement;
import descent.core.domX.IASTVisitor;

public abstract class ASTNode extends TreeNode<ASTNode, IASTVisitor> implements IDescentElement, IASTNode {

	public static final ASTNode[] NO_ELEMENTS = new ASTNode[0]; 
	
	/** Returns the node's children, ordered. */
	public ASTNode[] getChildren() {
		return (ASTNode[]) ASTChildrenCollector.getChildrenArray(this);
	}

	/** A character index into the original source string, 
	 * or <code>-1</code> if no source position information is available
	 * for this node; <code>-1</code> by default.
	 */
	public int startPos = -1;
	/** A character length, or <code>0</code> if no source position
	 * information is recorded for this node; <code>0</code> by default.
	 */
	public int length = 0;

	
	/** Gets the source range start position, aka offset. */
	public int getStartPos() {
		return startPos;
	}
	/** Gets the source range start position, aka offset. */
	public int getOffset() {
		return startPos;
	}
	/** Gets the source range length. */
	public int getLength() {
		return length;
	}
	
	/** Gets the source range end position (start position + length). */
	public int getEndPos() {
		Assert.isTrue(startPos != -1);
		return startPos+length;
	}
	/** Sets the source range end position (start position + length). */
	public void setEndPos(int endPos) {
		AssertIn.isTrue(endPos >= startPos);
		Assert.isTrue(startPos != -1);
		length = endPos - startPos ;
	}

	/** Sets the source range of the original source file where the source
	 * fragment corresponding to this node was found.
	 */
	public final void setSourceRange(int startPosition, int length) {
		//AssertIn.isTrue(startPosition >= 0 && length > 0);
		// source positions are not considered a structural property
		// but we protect them nevertheless
		//checkModifiable();
		this.startPos = startPosition;
		this.length = length;
	}
	
	/** Checks if the node has no defined source range info. */
	public boolean hasNoSourceRangeInfo() {
		return startPos == -1;
	}
	
	/** Returns a simple string representation of the node. */
	public String toString() {
		return "";
	}
	
	/** => #toStringNodeExtra(node, true) */
	public String toStringNodeExtra() {
		return ASTPrinter.toStringNodeExtra(this,true);
	}

	/** Gets an extended String representation of given node. (for debugging) */
	public String toStringNodeExtra(boolean printRangeInfo) {
		return ASTPrinter.toStringNodeExtra(this, printRangeInfo);
	}
}