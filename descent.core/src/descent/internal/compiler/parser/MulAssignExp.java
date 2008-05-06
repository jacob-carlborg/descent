package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;
import static descent.internal.compiler.parser.Constfold.Mul;

// DMD 1.020
public class MulAssignExp extends BinExp {

	public MulAssignExp(Loc loc, Expression e1, Expression e2) {
		super(loc, TOK.TOKmulass, e1, e2);
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
		return MUL_ASSIGN_EXP;
	}

	@Override
	public Expression interpret(InterState istate, SemanticContext context) {
		return interpretAssignCommon(istate, Mul, context);
	}

	@Override
	public char[] opId() {
		return Id.mulass;
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

		e1 = e1.modifiableLvalue(sc, e1, context);
		e1.checkScalar(context);
		e1.checkNoBool(context);
		type = e1.type;
		typeCombine(sc, context);
		e1.checkArithmetic(context);
		e2.checkArithmetic(context);
		if (e2.type.isfloating()) {
			Type t1;
			Type t2;

			t1 = e1.type;
			t2 = e2.type;
			if (t1.isreal()) {
				if (t2.isimaginary() || t2.iscomplex()) {
					e2 = e2.castTo(sc, t1, context);
				}
			} else if (t1.isimaginary()) {
				if (t2.isimaginary() || t2.iscomplex()) {
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
				}
			}
		}
		
		return this;
	}

}
