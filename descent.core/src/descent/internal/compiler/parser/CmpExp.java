package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;
import static descent.internal.compiler.parser.Constfold.Cmp;

// DMD 1.020
public class CmpExp extends BinExp {

	public CmpExp(Loc loc, TOK op, Expression e1, Expression e2) {
		super(loc, op, e1, e2);
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
		return CMP_EXP;
	}

	@Override
	public Expression interpret(InterState istate, SemanticContext context) {
		return interpretCommon2(istate, Cmp, context);
	}

	@Override
	public boolean isBit() {
		return true;
	}

	@Override
	public boolean isCommutative() {
		return true;
	}

	@Override
	public char[] opId() {
		return Id.cmp;
	}

	@Override
	public Expression optimize(int result, SemanticContext context) {
		Expression e;

		e1 = e1.optimize(result, context);
		e2 = e2.optimize(result, context);
		if (e1.isConst() && e2.isConst()) {
			e = Cmp.call(op, type, this.e1, this.e2, context);
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
			e = new CmpExp(loc, op, e, new IntegerExp(loc, 0, Type.tint32));
			e = e.semantic(sc, context);
			return e;
		}

		typeCombine(sc, context);
		type = Type.tboolean;

		// Special handling for array comparisons
		t1 = e1.type.toBasetype(context);
		t2 = e2.type.toBasetype(context);
		if ((t1.ty == TY.Tarray || t1.ty == TY.Tsarray)
				&& (t2.ty == TY.Tarray || t2.ty == TY.Tsarray)) {
			if (!t1.next.equals(t2.next)) {
				context.acceptProblem(Problem.newSemanticTypeError(
						IProblem.ArrayComparisonTypeMismatch, this, new String[] { t1.next.toChars(context), t2.next.toChars(context) }));
			}
			e = this;
		} else if (t1.ty == TY.Tstruct || t2.ty == TY.Tstruct
				|| (t1.ty == TY.Tclass && t2.ty == TY.Tclass)) {
			if (t2.ty == TY.Tstruct) {
				context.acceptProblem(Problem.newSemanticTypeError(IProblem.NeedMemberFunctionOpCmpForSymbolToCompare, this, new String[] { t2
						.toDsymbol(sc, context).kind(), t2.toChars(context) }));
			} else {
				context.acceptProblem(Problem.newSemanticTypeError(IProblem.NeedMemberFunctionOpCmpForSymbolToCompare, this, new String[] { t1
						.toDsymbol(sc, context).kind(), t1.toChars(context) }));
			}
			e = this;
		} else if (t1.iscomplex() || t2.iscomplex()) {
			context.acceptProblem(Problem.newSemanticTypeError(IProblem.CompareNotDefinedForComplexOperands, this));
			e = new IntegerExp(loc, 0);
		} else {
			e = this;
		}

		return e;
	}

}
