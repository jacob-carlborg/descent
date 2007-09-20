package dtool.ast;

import melnorme.miscutil.Assert;
import descent.internal.compiler.parser.ast.ASTRangeLessNode;
import descent.internal.compiler.parser.ast.IASTNode;


/**
 * Finds the innermost element whose source range contains the offset.
 * An element is picked between element.startPos (inclusive) and  
 * element.endPos (inclusive).   
 */
public class ASTNodeFinder extends ASTNeoUpTreeVisitor {
	
	private int offset; 
	private boolean inclusiveEnd;
	private IASTNode match;
	
	public ASTNodeFinder(int offsetCursor, boolean inclusiveEnd) {
		this.offset = offsetCursor;
		this.inclusiveEnd = inclusiveEnd;
		this.match = null;
	}
	
	public static IASTNode findElement(IASTNode root, int offset) {
		return findElement(root, offset, true);
	}
	
	public static ASTNeoNode findElement(ASTNeoNode root, int offset) {
		return (ASTNeoNode) findElement(root, offset, true);
	}

	
	public static ASTNeoNode findNeoElement(ASTNeoNode root, int offset, boolean inclusiveEnd){
		return (ASTNeoNode) findElement(root, offset, inclusiveEnd);
	}
	/** Finds the node at the given offset, starting from root.
	 *  inclusiveEnd controls whether to match nodes whose end position 
	 *  is the same as the offset.*/
	public static IASTNode findElement(IASTNode root, int offset, boolean inclusiveEnd){
		if(root == null)
			return null;
		Assert.isTrue(!root.hasNoSourceRangeInfo());

		ASTNodeFinder aef = new ASTNodeFinder(offset, inclusiveEnd);

		if(!aef.matchesRangeStart(root) || !aef.matchesRangeEnd(root)) 
			return null;
		
		root.accept(aef);
		Assert.isNotNull(aef.match);
		return aef.match;
	}
	
	
	@Override
	public boolean visit(ASTRangeLessNode elem) {
		return true;
	}

	@Override
	public boolean visit(IASTNode elem) {
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
	
	private boolean matchesRangeStart(IASTNode elem) {
		return offset >= elem.getStartPos();
	}

	private boolean matchesRangeEnd(IASTNode elem) {
		return inclusiveEnd ? 
				offset <= elem.getEndPos() : offset < elem.getEndPos();
	}

	@Override
	public void endVisit(ASTRangeLessNode elem) { }
	@Override
	public void endVisit(ASTNeoNode elem) { }

}
