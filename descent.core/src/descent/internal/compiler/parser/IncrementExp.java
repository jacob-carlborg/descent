package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class IncrementExp extends AddAssignExp {

	public IncrementExp(Loc loc, Expression e1) {
		super(loc, e1, null);
	}
	
	@Override
	public int getNodeType() {
		return INCREMENT_EXP;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, e1);
		}
		visitor.endVisit(this);
	}

}
