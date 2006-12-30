package dtool.dom.ast;

import dtool.dom.base.ASTNode;


/**
 * Finds an element given the offset 
 */
public class ASTElementFinder extends ASTNeoVisitor {
	
	private int offset; 
	private ASTNode match;
	
	public ASTElementFinder(int offsetCursor) {
		this.offset = offsetCursor;
		this.match = null;
	}
	
	public static ASTNode findElement(ASTNode root, int offset){
		if(offset < root.getStartPos() || offset >= root.getEndPos()  ) 
			return null;
		
		ASTElementFinder aef = new ASTElementFinder(offset);
		root.accept(aef);
		return aef.match;
	}

	public boolean visit(ASTNode elem) {
		if(elem.hasNoSourceRangeInfo()) {
			//assert(false); 
			return true;
		} else if(offset >= elem.getEndPos() ) {
			// Don't descend, go to next Node			
			return false; 
		} else if(offset < elem.getStartPos() && match == null) {
			// Gone too far, match is parent.
			match = elem.getParent();
			return false; 
		}
		return true; //Descend
	}

	public void endVisit(ASTNode elem) {
	}

}

