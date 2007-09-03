package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class OrExp extends BinExp {

	public OrExp(Loc loc, Expression e1, Expression e2) {
		super(loc, TOK.TOKor, e1, e2);
	}

	@Override
	public int getNodeType() {
		return OR_EXP;
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
	public Expression semantic(Scope sc, SemanticContext context) {
		Expression e;

		if (null == type) {
			super.semanticp(sc, context);

			e = op_overload(sc, context);
			if (null != e)
				return e;

			if ((e1.type.toBasetype(context).ty == TY.Tbool)
					&& (e2.type.toBasetype(context).ty == TY.Tbool)) {
				type = e1.type;
				e = this;
			} else {
				typeCombine(sc, context);
				e1.checkIntegral(context);
				e2.checkIntegral(context);
			}
		}
		return this;
	}

	@Override
	public Expression interpret(InterState istate, SemanticContext context) {
		return interpretCommon(istate, op, context);
	}
}
