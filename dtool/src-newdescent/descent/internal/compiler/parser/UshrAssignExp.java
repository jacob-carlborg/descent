package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.domX.IASTVisitor;

public class UshrAssignExp extends BinExp {

	public UshrAssignExp(Loc loc, Expression e1, Expression e2) {
		super(loc, TOK.TOKushrass, e1, e2);
	}
	
	@Override
	public int getNodeType() {
		return USHR_ASSIGN_EXP;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, e1);
			TreeVisitor.acceptChildren(visitor, e2);
		}
		visitor.endVisit(this);
	}
}
