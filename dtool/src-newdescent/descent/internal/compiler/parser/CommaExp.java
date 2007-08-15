package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.domX.IASTVisitor;

public class CommaExp extends BinExp {

	public CommaExp(Loc loc, Expression e1, Expression e2) {
		super(loc, TOK.TOKcomma, e1, e2);
	}
	
	@Override
	public boolean isBool(boolean result) {
		return e2.isBool(result);
	}
	
	@Override
	public int getNodeType() {
		return COMMA_EXP;
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
