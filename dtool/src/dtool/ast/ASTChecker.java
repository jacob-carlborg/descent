package dtool.ast;

import melnorme.miscutil.Assert;
import descent.internal.compiler.parser.ast.ASTNode;
import descent.internal.compiler.parser.ast.ASTRangeLessNode;
import dtool.Logg;


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
	
	private boolean eventSourceRangeNoInfo(ASTNode elem) {
		Logg.astmodel.print("Source range no info on: ");
		Logg.astmodel.println(elem.toStringAsNode(true));
		return false;
	}
	
	private boolean eventSourceRangeStartPosBreach(ASTNode elem) {
		Logg.astmodel.print("Source range start pos error on: ");
		Logg.astmodel.println(elem.toStringAsNode(true));
		return false;
	}
	
	private void eventSourceRangeEndPosBreach(ASTNode elem) {
		Logg.astmodel.print("Source range end pos error on: ");
		Logg.astmodel.println(elem.toStringAsNode(true));
	}
	
	/* ====================================================== */
	@Override
	public boolean visit(ASTRangeLessNode elem) {
		Assert.fail("Got an unranged node."); return false;
	}
	@Override
	public void endVisit(ASTRangeLessNode elem) {
		Assert.fail("Got an unranged node.");
	}
	
	@Override
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

	@Override
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

