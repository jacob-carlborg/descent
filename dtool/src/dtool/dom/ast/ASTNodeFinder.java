package dtool.dom.ast;

import melnorme.miscutil.Assert;
import melnorme.miscutil.log.Logg;



/**
 * Finds the innermost element whose source range contains the offset.
 * An element is picked between element.startPos (inclusive) and  
 * element.endPos (inclusive).   
 */
public class ASTNodeFinder extends ASTNeoUpTreeVisitor {
	
	private int offset; 
	private ASTNode match;
	
	public ASTNodeFinder(int offsetCursor) {
		this.offset = offsetCursor;
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
		
		ASTNodeFinder aef = new ASTNodeFinder(offset);
		if(root == null)
			Logg.main.println("IMPOSSIBLE");
		Assert.isNotNull(root);
		root.accept(aef);
		return aef.match;
	}

	public boolean visit(ASTNode elem) {
		if(elem.hasNoSourceRangeInfo()) {
			//Assert.fail();
			return true; // Descend and search children.
		} else if(offset >= elem.getStartPos() && offset <= elem.getEndPos()) {
			// This node is the match, or is parent of the match.
			match = elem;
			return true; // Descend and search children.
		} else {
			// Match not here, don't bother descending.
			return false; 
		}
		
	}

	public void endVisit(ASTNode elem) {
	}

}

