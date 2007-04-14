package descent.internal.compiler.parser;

import static descent.internal.compiler.parser.TY.*;
import static descent.internal.compiler.parser.MATCH.*;
import static descent.internal.compiler.parser.TOK.*;

public class AddrExp extends UnaExp {

	public AddrExp(Loc loc, Expression e) {
		super(loc, TOK.TOKaddress, e);
	}

	@Override
	public Expression castTo(Scope sc, Type t, SemanticContext context) {
		Type tb;

		Expression e = this;

		tb = t.toBasetype(context);
		type = type.toBasetype(context);
		if (tb != type) {
			// Look for pointers to functions where the functions are
			// overloaded.
			VarExp ve;
			FuncDeclaration f;

			if (type.ty == Tpointer && type.next.ty == Tfunction
					&& tb.ty == Tpointer && tb.next.ty == Tfunction
					&& e1.op == TOKvar) {
				ve = (VarExp) e1;
				f = ve.var.isFuncDeclaration();
				if (f != null) {
					f = f.overloadExactMatch(tb.next, context);
					if (f != null) {
						e = new VarExp(loc, f);
						e.type = f.type;
						e = new AddrExp(loc, e);
						e.type = t;
						return e;
					}
				}
			}
			e = super.castTo(sc, t, context);
		}
		e.type = t;
		return e;
	}

	@Override
	public int getNodeType() {
		return ADDR_EXP;
	}

	@Override
	public MATCH implicitConvTo(Type t, SemanticContext context) {
		MATCH result;

		result = type.implicitConvTo(t, context);

		if (result == MATCHnomatch) {
			// Look for pointers to functions where the functions are
			// overloaded.
			VarExp ve;
			FuncDeclaration f;

			t = t.toBasetype(context);
			if (type.ty == Tpointer && type.next.ty == Tfunction
					&& t.ty == Tpointer && t.next.ty == Tfunction
					&& e1.op == TOKvar) {
				ve = (VarExp) e1;
				f = ve.var.isFuncDeclaration();
				if (f != null && f.overloadExactMatch(t.next, context) != null) {
					result = MATCHexact;
				}
			}
		}
		return result;
	}

	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		if (type == null) {
			super.semantic(sc, context);
			e1 = e1.toLvalue(sc, null, context);
			if (e1.type == null) {
				error("cannot take address of %s", e1.toChars());
				type = Type.tint32;
				return this;
			}
			type = e1.type.pointerTo(context);

			// See if this should really be a delegate
			if (e1.op == TOKdotvar) {
				DotVarExp dve = (DotVarExp) e1;
				FuncDeclaration f = dve.var.isFuncDeclaration();

				if (f != null) {
					Expression e;

					e = new DelegateExp(loc, dve.e1, f);
					e = e.semantic(sc, context);
					return e;
				}
			} else if (e1.op == TOKvar) {
				VarExp dve = (VarExp) e1;
				FuncDeclaration f = dve.var.isFuncDeclaration();

				if (f != null && f.isNested()) {
					Expression e;

					e = new DelegateExp(loc, e1, f);
					e = e.semantic(sc, context);
					return e;
				}
			} else if (e1.op == TOKarray) {
				if (e1.type.toBasetype(context).ty == Tbit) {
					error("cannot take address of bit in array");
				}
			}
			return optimize(WANTvalue);
		}
		return this;
	}

}
