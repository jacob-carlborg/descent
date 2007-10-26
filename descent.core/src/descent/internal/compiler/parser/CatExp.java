package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;
import static descent.internal.compiler.parser.Constfold.Cat;

// DMD 1.020
public class CatExp extends BinExp {

	public CatExp(Loc loc, Expression e1, Expression e2) {
		super(loc, TOK.TOKtilde, e1, e2);
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
		return CAT_EXP;
	}

	@Override
	public Expression interpret(InterState istate, SemanticContext context) {
		//Expression e;
		Expression e1;
		Expression e2;

		e1 = this.e1.interpret(istate, context);
		if (e1 == EXP_CANT_INTERPRET) {
			return EXP_CANT_INTERPRET; //goto Lcant;
		}
		e2 = this.e2.interpret(istate, context);
		if (e2 == EXP_CANT_INTERPRET) {
			return EXP_CANT_INTERPRET; //goto Lcant;
		}
		return Cat.call(type, e1, e2, context);

		//Lcant:
		//	return EXP_CANT_INTERPRET;
	}

	@Override
	public char[] opId() {
		return Id.cat;
	}

	@Override
	public char[] opId_r() {
		return Id.cat_r;
	}

	@Override
	public Expression optimize(int result, SemanticContext context) {
		Expression e;

		e1 = e1.optimize(result, context);
		e2 = e2.optimize(result, context);
		e = Cat.call(type, e1, e2, context);
		if (e == EXP_CANT_INTERPRET) {
			e = this;
		}
		return e;
	}

	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		if (null != type) {
			return this;
		}

		Expression e;

		super.semanticp(sc, context);
		e = op_overload(sc, context);
		if (null != e) {
			return e;
		}

		Type tb1 = e1.type.toBasetype(context);
		Type tb2 = e2.type.toBasetype(context);

		/* BUG: Should handle things like:
		 *	char c;
		 *	c ~ ' '
		 *	' ' ~ c;
		 */

		if ((tb1.ty == TY.Tsarray || tb1.ty == TY.Tarray)
				&& e2.type.singleton.equals(tb1.next.singleton)) {
			type = tb1.next.arrayOf(context);
			if (tb2.ty == TY.Tarray) {
				// Make e2 into [e2]
				Expressions elements = new Expressions(1);
				elements.add(e2);
				e2 = new ArrayLiteralExp(e2.loc, elements);
				e2.type = type;
			}
			return this;
		}

		else if ((tb2.ty == TY.Tsarray || tb2.ty == TY.Tarray)
				&& e1.type.singleton.equals(tb2.next.singleton)) {
			type = tb2.next.arrayOf(context);
			if (tb1.ty == TY.Tarray) {
				// Make e1 into [e1]
				Expressions elements = new Expressions(1);
				elements.add(e1);
				e1 = new ArrayLiteralExp(e1.loc, elements);
				e1.type = type;
			}
			return this;
		}

		typeCombine(sc, context);

		if (type.toBasetype(context).ty == TY.Tsarray) {
			type = type.toBasetype(context).next.arrayOf(context);
		}

		if (e1.op == TOK.TOKstring && e2.op == TOK.TOKstring) {
			e = optimize(WANTvalue, context);
		} else if (e1.type.singleton.equals(e2.type.singleton)
				&& (e1.type.toBasetype(context).ty == TY.Tarray || e1.type
						.toBasetype(context).ty == TY.Tsarray)) {
			e = this;
		} else {
			context.acceptProblem(Problem.newSemanticTypeError(
					IProblem.CanOnlyConcatenateArrays, 0, start, length,
					new String[] { e1.type.toChars(context),
							e2.type.toChars(context), }));
			type = Type.tint32;
			e = this;
		}
		e.type = e.type.semantic(loc, sc, context);
		return e;
	}
}
