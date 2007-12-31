package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;
import static descent.internal.compiler.parser.Constfold.Min;
import static descent.internal.compiler.parser.TOK.TOKsymoff;

// DMD 1.020
public class MinExp extends BinExp {

	public MinExp(Loc loc, Expression e1, Expression e2) {
		super(loc, TOK.TOKmin, e1, e2);
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
		return MIN_EXP;
	}

	@Override
	public Expression interpret(InterState istate, SemanticContext context) {
		return interpretCommon(istate, Min, context);
	}

	@Override
	public char[] opId() {
		return Id.sub;
	}

	@Override
	public char[] opId_r() {
		return Id.sub_r;
	}

	@Override
	public Expression optimize(int result, SemanticContext context) {
		Expression e;

		e1 = e1.optimize(result, context);
		e2 = e2.optimize(result, context);
		if (e1.isConst() && e2.isConst()) {
			if (e2.op == TOKsymoff) {
				return this;
			}
			e = Min.call(type, e1, e2, context);
		} else {
			e = this;
		}
		return e;
	}

	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		Expression e;
		Type t1;
		Type t2;

		if (null != type) {
			return this;
		}

		super.semanticp(sc, context);

		e = op_overload(sc, context);
		if (null != e) {
			return e;
		}

		e = this;
		t1 = e1.type.toBasetype(context);
		t2 = e2.type.toBasetype(context);
		if (t1.ty == TY.Tpointer) {
			if (t2.ty == TY.Tpointer) {
				int stride;
				Expression e_;

				typeCombine(sc, context); // make sure pointer types are compatible
				type = Type.tptrdiff_t;
				stride = t2.next.size(Loc.ZERO, context);
				e_ = new DivExp(loc, this, new IntegerExp(Loc.ZERO,
						new integer_t(stride), Type.tptrdiff_t));
				e_.type = Type.tptrdiff_t;
				e_.copySourceRange(this);
				return e_;
			} else if (t2.isintegral()) {
				e = scaleFactor(sc, context);
			} else {
				context.acceptProblem(Problem.newSemanticTypeError(
						IProblem.IncompatibleTypesForMinus, this));
				return new IntegerExp(loc, 0);
			}
		} else if (t2.ty == TY.Tpointer) {
			type = e2.type;
			context.acceptProblem(Problem.newSemanticTypeError(
					IProblem.CannotSubtractPointerFromSymbol, this, new String[] { e1.type.toChars(context) }));
			return new IntegerExp(loc, 0);
		} else {
			typeCombine(sc, context);
			t1 = e1.type.toBasetype(context);
			t2 = e2.type.toBasetype(context);
			if ((t1.isreal() && t2.isimaginary())
					|| (t1.isimaginary() && t2.isreal())) {
				switch (type.ty) {
				case Tfloat32:
				case Timaginary32:
					type = Type.tcomplex32;
					break;

				case Tfloat64:
				case Timaginary64:
					type = Type.tcomplex64;
					break;

				case Tfloat80:
				case Timaginary80:
					type = Type.tcomplex80;
					break;

				default:
					assert (false);
				}
			}
		}

		return e;
	}

}
