package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class ModAssignExp extends BinExp {

	public ModAssignExp(Loc loc, Expression e1, Expression e2) {
		super(loc, TOK.TOKmodass, e1, e2);
	}
	
	@Override
	public int getNodeType() {
		return MOD_ASSIGN_EXP;
	}

	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, e1);
			TreeVisitor.acceptChildren(visitor, e2);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public Expression semantic(Scope sc, SemanticContext context)
	{
		/* TODO semantic return commonSemanticAssign(sc, context); */
		return null;
	}
}
