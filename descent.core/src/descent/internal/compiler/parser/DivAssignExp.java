package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;
import static descent.internal.compiler.parser.Constfold.Div;

// DMD 1.020
public class DivAssignExp extends BinExp {

	public DivAssignExp(Loc loc, Expression e1, Expression e2) {
		super(loc, TOK.TOKdivass, e1, e2);
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
		return DIV_ASSIGN_EXP;
	}

	@Override
	public Expression interpret(InterState istate, SemanticContext context) {
		return interpretAssignCommon(istate, Div, context);
	}

	@Override
	public char[] opId() {
		return Id.divass;
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
		e1.checkArithmetic(context);
		e2.checkArithmetic(context);
		if (e2.type.isimaginary()) {
			Type t1 = null;
			Type t2 = null;

			t1 = e1.type;
			if (t1.isreal()) {
				// x/iv = i(-x/v)
				// Therefore, the result is 0
				e2 = new CommaExp(loc, e2, new RealExp(loc, new char[0],
						real_t.ZERO, t1));
				e2.type = t1;
				e = new AssignExp(loc, e1, e2);
				e.type = t1;
				return e;
			} else if (t1.isimaginary()) {
				switch (t1.ty) {
				case Timaginary32:
					t2 = Type.tfloat32;
					break;
				case Timaginary64:
					t2 = Type.tfloat64;
					break;
				case Timaginary80:
					t2 = Type.tfloat80;
					break;
				default:
					assert (false);
				}
				e2 = e2.castTo(sc, t2, context);
				e = new AssignExp(loc, e1, e2);
				e.type = t1;
				return e;
			}
		}
		
		return this;
	}

}
