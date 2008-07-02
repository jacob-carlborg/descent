package descent.internal.compiler.parser;

import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;

import static descent.internal.compiler.parser.TY.Tdelegate;
import static descent.internal.compiler.parser.TY.Tfunction;


public class DelegateExp extends UnaExp {

	public FuncDeclaration func;

	public DelegateExp(Loc loc, Expression e, FuncDeclaration f) {
		super(loc, TOK.TOKdelegate, e);
		this.func = f;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}

	@Override
	public Expression castTo(Scope sc, Type t, SemanticContext context) {
		Type tb;
		Expression e = this;

		tb = t.toBasetype(context);
		type = type.toBasetype(context);
		if (!same(tb, type, context)) {
			// Look for delegates to functions where the functions are
			// overloaded.
			FuncDeclaration f;

			if (type.ty == Tdelegate && type.next.ty == Tfunction
					&& tb.ty == Tdelegate && tb.next.ty == Tfunction) {
				if (func != null) {
					f = func.overloadExactMatch(tb.next, context);
					if (f != null) {
						int[] offset = { 0 };
						if (f.tintro() != null
								&& f.tintro().next.isBaseOf(f.type.next, offset,
										context) && offset[0] != 0) {
							if (context.acceptsErrors()) {
								context.acceptProblem(Problem.newSemanticTypeError(
										IProblem.CannotFormDelegateDueToCovariantReturnType, this));
							}
						}
						e = new DelegateExp(loc, e1, f);
						e.type = t;
						return e;
					}
					if (func.tintro() != null) {
						if (context.acceptsErrors()) {
							context.acceptProblem(Problem.newSemanticTypeError(
									IProblem.CannotFormDelegateDueToCovariantReturnType, this));
						}
					}
				}
			}
			e = super.castTo(sc, t, context);
		} else {
			int[] offset = { 0 };

			if (func.tintro() != null
					&& func.tintro().next.isBaseOf(func.type.next, offset,
							context) && offset[0] != 0) {
				if (context.acceptsErrors()) {
					context.acceptProblem(Problem.newSemanticTypeError(
							IProblem.CannotFormDelegateDueToCovariantReturnType, this));
				}
			}
		}
		e.type = t;
		return e;
	}

	@Override
	public int getNodeType() {
		return DELEGATE_EXP;
	}

	@Override
	public MATCH implicitConvTo(Type t, SemanticContext context) {
		MATCH result;

		result = type.implicitConvTo(t, context);

		if (result.ordinal() == 0) {
			// Look for pointers to functions where the functions are
			// overloaded.

			t = t.toBasetype(context);
			if (type.ty == Tdelegate && type.next.ty == Tfunction
					&& t.ty == Tdelegate && t.next.ty == Tfunction) {
				if (func != null
						&& func.overloadExactMatch(t.next, context) != null) {
					result = MATCH.MATCHexact;
				}
			}
		}
		return result;
	}

	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		if (type == null) {
			e1 = e1.semantic(sc, context);
			type = new TypeDelegate(func.type);
			type = type.semantic(loc, sc, context);
			AggregateDeclaration ad = func.toParent().isAggregateDeclaration();
			if (func.needThis()) {
			    e1 = getRightThis(loc, sc, ad, e1, func, context);
			}
		}
		return this;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		buf.writeByte('&');
		if (!func.isNested()) {
			expToCBuffer(buf, hgs, e1, PREC.PREC_primary, context);
			buf.writeByte('.');
		}
		buf.writestring(func.toChars(context));
	}

}
