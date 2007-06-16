package dtool.dom.ast;



/**
 * Finds the innermost element whose source range contains the offset.
 * An element is picked between element.startPos (inclusive) and  
 * element.endPos (inclusive).   
 */
public class ASTElementFinder extends ASTNeoUpTreeVisitor {
	
	private int offset; 
	private ASTNode match;
	
	public ASTElementFinder(int offsetCursor) {
		this.offset = offsetCursor;
		this.match = null;
	}
	
	public static ASTNode findElement(ASTNode root, int offset){
		if(offset < root.getStartPos() || offset > root.getEndPos() ) 
			return null;
		
		ASTElementFinder aef = new ASTElementFinder(offset);
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

