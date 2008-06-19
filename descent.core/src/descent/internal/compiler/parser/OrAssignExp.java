package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;
import static descent.internal.compiler.parser.Constfold.Or;


public class OrAssignExp extends BinExp {

	public OrAssignExp(Loc loc, Expression e1, Expression e2) {
		super(loc, TOK.TOKorass, e1, e2);
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, sourceE1);
			TreeVisitor.acceptChildren(visitor, sourceE2);
		}
		visitor.endVisit(this);
	}

	@Override
	public int getNodeType() {
		return OR_ASSIGN_EXP;
	}

	@Override
	public Expression interpret(InterState istate, SemanticContext context) {
		return interpretAssignCommon(istate, Or, context);
	}

	@Override
	public char[] opId(SemanticContext context) {
		return Id.orass;
	}

	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		return commonSemanticAssignIntegral(sc, context);
	}

}
