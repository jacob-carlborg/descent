package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;
import static descent.internal.compiler.parser.Constfold.Or;

// DMD 1.020
public class OrExp extends BinExp {

	public OrExp(Loc loc, Expression e1, Expression e2) {
		super(loc, TOK.TOKor, e1, e2);
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
		return OR_EXP;
	}

	@Override
	public Expression interpret(InterState istate, SemanticContext context) {
		return interpretCommon(istate, Or, context);
	}

	@Override
	public boolean isCommutative() {
		return true;
	}

	@Override
	public char[] opId() {
		return Id.ior;
	}

	@Override
	public char[] opId_r() {
		return Id.ior_r;
	}

	@Override
	public Expression optimize(int result, SemanticContext context) {
		Expression e;

		e1 = e1.optimize(result, context);
		e2 = e2.optimize(result, context);
		if (e1.isConst() && e2.isConst()) {
			e = Or.call(type, e1, e2, context);
		} else {
			e = this;
		}
		return e;
	}

	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		Expression e;

		if (null == type) {
			super.semanticp(sc, context);

			e = op_overload(sc, context);
			if (null != e) {
				assignBinding();
				return e;
			}

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
		
		assignBinding();
		return this;
	}

}
