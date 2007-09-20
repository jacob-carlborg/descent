package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class UshrAssignExp extends BinExp {

	public UshrAssignExp(Loc loc, Expression e1, Expression e2) {
		super(loc, TOK.TOKushrass, e1, e2);
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
		return USHR_ASSIGN_EXP;
	}
	
	@Override
	public Expression interpret(InterState istate, SemanticContext context) {
		return interpretCommon2(istate, op, context);
	}

	@Override
	public char[] opId() {
		return Id.ushrass;
	}
	
	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		Expression e;

		super.semantic(sc, context);
		e2 = resolveProperties(sc, e2, context);

		e = op_overload(sc, context);
		if (null != e) {
			return e;
		}

		e1 = e1.modifiableLvalue(sc, null, context);
		e1.checkScalar(context);
		e1.checkNoBool(context);
		type = e1.type;
		typeCombine(sc, context);
		e1.checkIntegral(context);
		e2 = e2.checkIntegral(context);
		e2 = e2.castTo(sc, Type.tshiftcnt, context);
		return this;
	}

}