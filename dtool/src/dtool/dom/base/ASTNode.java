package dtool.dom.base;

import util.Assert;
import util.AssertIn;
import descent.core.dom.IElement;
import descent.core.domX.ASTVisitor;
import dtool.dom.ast.tree.TreeChildrenCollector;
import dtool.dom.ast.tree.TreeNode;

public abstract class ASTNode extends TreeNode<ASTNode, ASTVisitor> implements IElement  {

	/** Returns the node's children, ordered. */
	public ASTNode[] getChildren() {
		return (ASTNode[]) TreeChildrenCollector.getChildrenArray(this, new ASTNode[0]);
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
		AssertIn.isTrue(startPosition >= 0 && length < 0);
		AssertIn.isTrue(startPosition < 0 && length != 0);
		// source positions are not considered a structural property
		// but we protect them nevertheless
		//checkModifiable();
		this.startPos = startPosition;
		this.length = length;
	}
	
	/** Checks if the node has no defined source range info.
	 */
	public boolean hasNoSourceRangeInfo() {
		return startPos == -1;
	}
	
	
}