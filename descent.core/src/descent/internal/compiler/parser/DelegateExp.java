package descent.internal.compiler.parser;

import static descent.internal.compiler.parser.TY.*;

public class DelegateExp extends UnaExp {

	public FuncDeclaration func;

	public DelegateExp(Expression e, FuncDeclaration func) {
		super(TOK.TOKdelegate, e);
		this.func = func;
	}
	
	@Override
	public Expression castTo(Scope sc, Type t, SemanticContext context) {
		Type tb;
		Expression e = this;

		tb = t.toBasetype(context);
		type = type.toBasetype(context);
		if (tb != type) {
			// Look for delegates to functions where the functions are
			// overloaded.
			FuncDeclaration f;

			if (type.ty == Tdelegate && type.next.ty == Tfunction
					&& tb.ty == Tdelegate && tb.next.ty == Tfunction) {
				if (func != null) {
					f = func.overloadExactMatch(tb.next, context);
					if (f != null) {
						int[] offset = { 0 };
						if (f.tintro != null
								&& f.tintro.next.isBaseOf(f.type.next, offset)
								&& offset[0] != 0)
							error("cannot form delegate due to covariant return type");
						e = new DelegateExp(e1, f);
						e.type = t;
						return e;
					}
					if (func.tintro != null)
						error("cannot form delegate due to covariant return type");
				}
			}
			e = super.castTo(sc, t, context);
		} else {
			int[] offset = { 0 };

			if (func.tintro != null
					&& func.tintro.next.isBaseOf(func.type.next, offset)
					&& offset[0] != 0)
				error("cannot form delegate due to covariant return type");
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

}
