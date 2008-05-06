package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;
import static descent.internal.compiler.parser.Constfold.Cat;

// DMD 1.020
public class CatAssignExp extends BinExp {

	public CatAssignExp(Loc loc, Expression e1, Expression e2) {
		super(loc, TOK.TOKcatass, e1, e2);
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
		return CAT_ASSIGN_EXP;
	}

	@Override
	public Expression interpret(InterState istate, SemanticContext context) {
		return interpretAssignCommon(istate, Cat, context);
	}

	@Override
	public char[] opId() {
		return Id.catass;
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

		if (e1.op == TOK.TOKslice) {
			SliceExp se = (SliceExp) e1;

			if (se.e1.type.toBasetype(context).ty == TY.Tsarray) {
				if (context.acceptsProblems()) {
					context.acceptProblem(Problem.newSemanticTypeError(IProblem.CannotAppendToStaticArray, this, new String[] { se.e1.type.toChars(context) }));
				}
			}
		}

		e1 = e1.modifiableLvalue(sc, e1, context);

		Type tb1 = e1.type.toBasetype(context);
		Type tb2 = e2.type.toBasetype(context);
		
		e2.rvalue(context);

		if ((tb1.ty == TY.Tarray)
				&& (tb2.ty == TY.Tarray || tb2.ty == TY.Tsarray)
				&& MATCH.MATCHnomatch != (e2.implicitConvTo(e1.type, context))) {
			// Append array
			e2 = e2.castTo(sc, e1.type, context);
			type = e1.type;
			e = this;
		}

		else if ((tb1.ty == TY.Tarray)
				&& null != e2.implicitConvTo(tb1.next, context)) {
			// Append element
			e2 = e2.castTo(sc, tb1.next, context);
			type = e1.type;
			e = this;
		}

		else {
			if (context.acceptsProblems()) {
				context.acceptProblem(Problem.newSemanticTypeError(IProblem.CannotAppendTypeToType, this, new String[] { tb2.toChars(context), tb1
						.toChars(context) }));
			}
			type = Type.tint32;
			e = this;
		}

		return e;
	}

}
