package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;
import static descent.internal.compiler.parser.Constfold.Min;

// DMD 1.020
public class MinAssignExp extends BinExp {
	
	public boolean isPreDecrement;

	public MinAssignExp(Loc loc, Expression e1, Expression e2) {
		super(loc, TOK.TOKminass, e1, e2);
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
		return MIN_ASSIGN_EXP;
	}

	@Override
	public Expression interpret(InterState istate, SemanticContext context) {
		return interpretAssignCommon(istate, Min, context);
	}

	@Override
	public char[] opId() {
		return Id.subass;
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

		e1 = e1.modifiableLvalue(sc, e1, context);
		e1.checkScalar(context);
		e1.checkNoBool(context);
		if (e1.type.ty == TY.Tpointer && e2.type.isintegral()) {
			e = scaleFactor(sc, context);
		} else {
			e1.checkArithmetic(context);
			e2.checkArithmetic(context);
			type = e1.type;
			typeCombine(sc, context);
			if (type.isreal() || type.isimaginary()) {
				assert (e2.type.isfloating());
				e2 = e2.castTo(sc, e1.type, context);
			}
			e = this;
		}
		
		return e;
	}

}
