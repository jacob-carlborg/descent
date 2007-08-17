package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class CatExp extends BinExp {

	public CatExp(Loc loc, Expression e1, Expression e2) {
		super(loc, TOK.TOKtilde, e1, e2);
	}
	
	@Override
	public int getNodeType() {
		return CAT_EXP;
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
