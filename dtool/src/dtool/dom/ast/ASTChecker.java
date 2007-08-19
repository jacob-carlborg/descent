package dtool.dom.ast;

import melnorme.miscutil.Assert;
import descent.internal.compiler.parser.ast.ASTNode;
import descent.internal.compiler.parser.ast.ASTRangeLessNode;


/**
 * Checks for AST validity. Namely:
 * Source range consistency. 
 */
public class ASTChecker extends ASTNeoUpTreeVisitor {
	
	private int offsetCursor;
	protected StringBuffer strbuffer;
	
	public ASTChecker(int offsetCursor) {
		this.offsetCursor = offsetCursor;
	}
	
	/** Checks an AST for errors, such as source range errors. */
	public static void checkConsistency(ASTNeoNode elem){
		elem.accept(new ASTChecker(elem.getStartPos()));
	}	
	
	protected void print(String str) {
		System.out.print(str);
	}
	
	protected void println(String str) {
		System.out.print(str);
		System.out.println();
	}

	private boolean eventSourceRangeNoInfo(ASTNode elem) {
		print("Source range no info on: ");
		println(ASTPrinter.toStringNodeExtra(elem));
		return false;
	}
	
	private boolean eventSourceRangeStartPosBreach(ASTNode elem) {
		print("Source range start pos error on: ");
		println(ASTPrinter.toStringNodeExtra(elem));
		return false;
	}
	
	private void eventSourceRangeEndPosBreach(ASTNode elem) {
		print("Source range end pos error on: ");
		println(ASTPrinter.toStringNodeExtra(elem));
	}
	
	/* ====================================================== */
	
	public boolean visit(ASTRangeLessNode elem) {
		Assert.fail("Got an unranged node."); return false;
	}
	
	public void endVisit(ASTRangeLessNode elem) {
		Assert.fail("Got an unranged node.");
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
}

