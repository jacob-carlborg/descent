package descent.core.domX;

import descent.internal.compiler.parser.ASTDmdNode;
import dtool.dom.ast.ASTChildrenCollector;
import dtool.dom.ast.IASTNode;
import melnorme.miscutil.Assert;
import melnorme.miscutil.AssertIn;
import melnorme.miscutil.tree.TreeNode;

public abstract class ASTNode extends TreeNode<ASTNode, IASTVisitor> implements IASTNode {
	
	public static final ASTNode[] NO_ELEMENTS = new ASTNode[0]; 
	
	/** A character index into the original source string, 
	 * or <code>-1</code> if no source position information is available
	 * for this node; <code>-1</code> by default.
	 */
	public int start = -1;
	/** A character length, or <code>0</code> if no source position
	 * information is recorded for this node; <code>0</code> by default.
	 */
	public int length = 0;

	
	/** Gets the source range start position, aka offset. */
	public int getStartPos() {
		return start;
	}
	/** Gets the source range start position, aka offset. */
	public int getOffset() {
		return start;
	}
	/** Gets the source range length. */
	public int getLength() {
		return length;
	}
	
	/** Gets the source range end position (start position + length). */
	public int getEndPos() {
		Assert.isTrue(start != -1);
		return start+length;
	}
	/** Sets the source range end position (start position + length). */
	public void setEndPos(int endPos) {
		AssertIn.isTrue(endPos >= start);
		Assert.isTrue(start != -1);
		length = endPos - start ;
	}

	/** Sets the source range of the original source file where the source
	 * fragment corresponding to this node was found.
	 */
	public final void setSourceRange(int startPosition, int length) {
		//AssertIn.isTrue(startPosition >= 0 && length > 0);
		// source positions are not considered a structural property
		// but we protect them nevertheless
		//checkModifiable();
		this.start = startPosition;
		this.length = length;
	}
	
	/** Checks if the node has no defined source range info. */
	public boolean hasNoSourceRangeInfo() {
		return start == -1;
	}
	
	/** Returns the node's children, ordered. */
	public ASTNode[] getChildren() {
		return (ASTNode[]) ASTChildrenCollector.getChildrenArray(this);
	}

	/** Returns a simple string representation of the node. */
	public String toString() {
		return toStringAsNode(false);
	}

	
	/** Gets an extended String representation of given node. (for debugging) */
	public String toStringAsNode(boolean printRangeInfo) {
		String str = toStringClassName();

		if(this instanceof ASTDmdNode)
			str = "#" + str;
		if(printRangeInfo)
			str += " ["+ start +"+"+ length +"]";
		return str;
	}

}
