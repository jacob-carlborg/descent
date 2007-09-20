package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;
import static descent.internal.compiler.parser.TOK.*;
import static descent.internal.compiler.parser.Constfold.Add;

// DMD 1.020 
public class AddExp extends BinExp {

	public AddExp(Loc loc, Expression e1, Expression e2) {
		super(loc, TOK.TOKadd, e1, e2);
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
		return ADD_EXP;
	}
	
	@Override
	public Expression interpret(InterState istate, SemanticContext context) {
		return interpretCommon(istate, op, context);
	}

	@Override
	public boolean isCommutative() {
		return true;
	}

	@Override
	public char[] opId() {
		return Id.add;
	}

	@Override
	public char[] opId_r() {
		return Id.add_r;
	}

	@Override
	public Expression optimize(int result, SemanticContext context) {
		Expression e;

		e1 = e1.optimize(result, context);
		e2 = e2.optimize(result, context);
		if (e1.isConst() && e2.isConst()) {
			if (e1.op == TOKsymoff && e2.op == TOKsymoff) {
				return this;
			}
			e = Add.call(type, e1, e2, context);
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
				return e;
			}

			Type tb1 = e1.type.toBasetype(context);
			Type tb2 = e2.type.toBasetype(context);

			if ((tb1.ty == TY.Tarray || tb1.ty == TY.Tsarray)
					&& (tb2.ty == TY.Tarray || tb2.ty == TY.Tsarray)
					&& (tb1.next.equals(tb2.next))) {
				type = e1.type;
				e = this;
			} else if ((tb1.ty == TY.Tpointer && e2.type.isintegral())
					|| (tb2.ty == TY.Tpointer && e1.type.isintegral())) {
				e = scaleFactor(sc, context);
			} else if (tb1.ty == TY.Tpointer && tb2.ty == TY.Tpointer) {
				incompatibleTypes(context);
				type = e1.type;
				e = this;
			} else {
				typeCombine(sc, context);
				if ((e1.type.isreal() && e2.type.isimaginary())
						|| (e1.type.isimaginary() && e2.type.isreal())) {
					switch (type.toBasetype(context).ty) {
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
				e = this;
			}
			return e;
		}

		return this;
	}

}