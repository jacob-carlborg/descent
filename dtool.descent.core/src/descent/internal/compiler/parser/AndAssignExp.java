package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 1.020
public class AndAssignExp extends BinExp {

	public AndAssignExp(Loc loc, Expression e1, Expression e2) {
		super(loc, TOK.TOKandass, e1, e2);
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, e1);
			TreeVisitor.acceptChildren(visitor, e2);
		}
		visitor.endVisit(this);
	}

	@Override
	public int getNodeType() {
		return AND_ASSIGN_EXP;
	}

	@Override
	public Expression interpret(InterState istate, SemanticContext context) {
		return interpretAssignCommon(istate, op, context);
	}

	@Override
	public char[] opId() {
		return Id.addass;
	}

	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		return commonSemanticAssignIntegral(sc, context);
	}

}