package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;
import static descent.internal.compiler.parser.Constfold.Shl;

// DMD 1.020
public class ShlExp extends BinExp {

	public ShlExp(Loc loc, Expression e1, Expression e2) {
		super(loc, TOK.TOKshl, e1, e2);
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
		return SHL_EXP;
	}
	
	@Override
	public Expression interpret(InterState istate, SemanticContext context) {
		return interpretCommon(istate, Shl, context);
	}

	@Override
	public char[] opId() {
		return Id.shl;
	}

	@Override
	public char[] opId_r() {
		return Id.shl_r;
	}

	@Override
	public Expression optimize(int result, SemanticContext context) {
		Expression e;

		e1 = e1.optimize(result, context);
		e2 = e2.optimize(result, context);
		e = this;
		if (e2.isConst()) {
			integer_t i2 = e2.toInteger(context);
			if (i2.compareTo(0) < 0
					|| i2.compareTo(e1.type.size(context) * 8) > 0) {
				context.acceptProblem(Problem.newSemanticTypeError(
						IProblem.ShiftLeftExceeds, 0, start,
						length, new String[] { i2.toString(), String.valueOf(e2.type.size(context) * 8) }));
				e2 = new IntegerExp(0);
			}
			if (e1.isConst()) {
				e = new IntegerExp(loc, e1.toInteger(context).shiftLeft(
						e2.toInteger(context)), type);
			}
		}
		return e;
	}

	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		if (null == type) {
			Expression e;

			super.semanticp(sc, context);
			e = op_overload(sc, context);
			if (null != e) {
				assignBinding();
				return e;
			}
			e1 = e1.checkIntegral(context);
			e2 = e2.checkIntegral(context);
			e1 = e1.integralPromotions(sc, context);
			e2 = e2.castTo(sc, Type.tshiftcnt, context);
			type = e1.type;
		}
		
		assignBinding();
		return this;
	}

}
