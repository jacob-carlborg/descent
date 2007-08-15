package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.domX.IASTVisitor;

public class ModExp extends BinExp {

	public ModExp(Loc loc, Expression e1, Expression e2) {
		super(loc, TOK.TOKmod, e1, e2);
	}
	
	@Override
	public int getNodeType() {
		return MOD_EXP;
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
