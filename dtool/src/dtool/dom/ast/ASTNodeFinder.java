package dtool.dom.ast;

import melnorme.miscutil.Assert;



/**
 * Finds the innermost element whose source range contains the offset.
 * An element is picked between element.startPos (inclusive) and  
 * element.endPos (inclusive).   
 */
public class ASTNodeFinder extends ASTNeoUpTreeVisitor {
	
	private int offset; 
	private boolean inclusiveEnd;
	private ASTNode match;
	
	public ASTNodeFinder(int offsetCursor, boolean inclusiveEnd) {
		this.offset = offsetCursor;
		this.inclusiveEnd = inclusiveEnd;
		this.match = null;
	}
	
	public static ASTNode findElement(ASTNode root, int offset) {
		return findElement(root, offset, true);
	}

	/** Finds the node at the given offset, starting from root.
	 *  inclusiveEnd controls whether to match nodes whose end position 
	 *  is the same as the offset.*/
	public static ASTNode findElement(ASTNode root, int offset, boolean inclusiveEnd){
		if(offset < root.getStartPos() || offset > root.getEndPos() ) 
			return null;
		
		ASTNodeFinder aef = new ASTNodeFinder(offset, inclusiveEnd);
		Assert.isNotNull(root);
		root.accept(aef);
		Assert.isNotNull(aef.match);
		return aef.match;
	}

	public boolean visit(ASTNode elem) {
		if(elem.hasNoSourceRangeInfo()) {
			//Assert.fail();
			return true; // Descend and search children.
		} else if(matchesRangeStart(elem) && matchesRangeEnd(elem)) {
			// This node is the match, or is parent of the match.
			match = elem;
			return true; // Descend and search children.
		} else {
			// Match not here, don't bother descending.
			return false; 
		}
		
	}
	
	private boolean matchesRangeStart(ASTNode elem) {
		return offset >= elem.getStartPos();
	}

	private boolean matchesRangeEnd(ASTNode elem) {
		return inclusiveEnd ? 
				offset <= elem.getEndPos() : offset < elem.getEndPos();
	}


	public void endVisit(ASTNode elem) {
	}

}

