package dtool.dom.ext;

import descent.core.domX.ASTNode;

/**
 * Checks for AST validity. Namely:
 * Source range consistency. 
 */
public class ASTChecker extends ASTNeoVisitor {
	
	private int offsetCursor;
	
	public ASTChecker(int offsetCursor) {
		this.offsetCursor = offsetCursor;
	}
	
	public static void checkConsistency(ASTNode elem){
		elem.accept(new ASTChecker(elem.getStartPos()));
	}
	
	
	public boolean visit(ASTNode elem) {
		if(elem.hasNoSourceRangeInfo()) {
			return eventSourceRangeNoInfo(elem);
		} else if(elem.getOffset() < offsetCursor) {
			return eventSourceRangeStartPosBreach(elem);
		} else {
			offsetCursor = elem.getOffset();
			return true;
		}
	}

	public void endVisit(ASTNode elem) {
		if(elem.hasNoSourceRangeInfo()) {
			//return eventSourceRangeNoInfo(elem);
			return;
		} else if(elem.getEndPos() < offsetCursor) {
			eventSourceRangeEndPosBreach(elem);
			return;
		} else {
			offsetCursor = elem.getEndPos();
			return;
		}
	}

	private boolean eventSourceRangeNoInfo(ASTNode elem) {
		System.out.print("Source range no info on: ");
		ASTPrinter.printSingleElement(elem);
		return false;
	}

	
	private boolean eventSourceRangeStartPosBreach(ASTNode elem) {
		System.out.print("Source range start pos error on: ");
		ASTPrinter.printSingleElement(elem);
		return false;
	}
	
	private void eventSourceRangeEndPosBreach(ASTNode elem) {
		System.out.print("Source range end pos error on: ");
		ASTPrinter.printSingleElement(elem);
	}
	
}

