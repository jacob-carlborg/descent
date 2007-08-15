package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.domX.IASTVisitor;

public class DecrementExp extends MinAssignExp {

	public DecrementExp(Loc loc, Expression e1) {
		super(loc, e1, null);
	}
	
	@Override
	public int getNodeType() {
		return DECREMENT_EXP;
	}
	
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, e1);
		}
		visitor.endVisit(this);
	}

}
