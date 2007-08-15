package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.domX.IASTVisitor;

public class AddAssignExp extends BinExp {

	public AddAssignExp(Loc loc, Expression e1, Expression e2) {
		super(loc, TOK.TOKaddass, e1, e2);
	}
	
	@Override
	public int getNodeType() {
		return ADD_ASSIGN_EXP;
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
