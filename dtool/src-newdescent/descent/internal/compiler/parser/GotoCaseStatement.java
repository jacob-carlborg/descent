package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.domX.IASTVisitor;

public class GotoCaseStatement extends Statement {

	public Expression exp;

	public GotoCaseStatement(Loc loc, Expression exp) {
		super(loc);
		this.exp = exp;		
	}
	
	@Override
	public int getNodeType() {
		return GOTO_CASE_STATEMENT;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, exp);
		}
		visitor.endVisit(this);
	}


}
