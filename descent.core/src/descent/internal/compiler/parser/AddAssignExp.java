package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;
import static descent.internal.compiler.parser.Constfold.Add;

// DMD 1.020 
public class AddAssignExp extends BinExp {
	
	public boolean isPreIncrement;
	
	public AddAssignExp(Loc loc, Expression e1, Expression e2) {
		super(loc, TOK.TOKaddass, e1, e2);
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
		return ADD_ASSIGN_EXP;
	}
	
	@Override
	public Expression interpret(InterState istate, SemanticContext context) {
		return interpretAssignCommon(istate, Add, context);
	}

	@Override
	public char[] opId() {
		return Id.addass;
	}

	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		Expression e;

		if (null != type) {
			return this;
		}

		super.semantic(sc, context);
		e2 = resolveProperties(sc, e2, context);

		e = op_overload(sc, context);
		if (null != e) {
			return e;
		}

		e1 = e1.modifiableLvalue(sc, null, context);

		Type tb1 = e1.type.toBasetype(context);
		Type tb2 = e2.type.toBasetype(context);

		if ((tb1.ty == TY.Tarray || tb1.ty == TY.Tsarray)
				&& (tb2.ty == TY.Tarray || tb2.ty == TY.Tsarray)
				&& (tb1.next.equals(tb2.next))) {
			type = e1.type;
			e = this;
		} else {
			e1.checkScalar(context);
			e1.checkNoBool(context);
			if (tb1.ty == TY.Tpointer && tb2.isintegral()) {
				e = scaleFactor(sc, context);
			} else if (tb1.ty == TY.Tbit || tb1.ty == TY.Tbool) {
				// Rewrite e1+=e2 to e1=e1+e2
				e = new AddExp(loc, e1, e2);
				e = new CastExp(loc, e, e1.type);
				e = new AssignExp(loc, e1.syntaxCopy(context), e);
				e = e.semantic(sc, context);
			} else {
				type = e1.type;
				typeCombine(sc, context);
				e1.checkArithmetic(context);
				e2.checkArithmetic(context);
				if (type.isreal() || type.isimaginary()) {
					// assert(global.errors || e2->type->isfloating());
					e2 = e2.castTo(sc, e1.type, context);
				}
				e = this;
			}
		}

		return e;
	}

}
