package descent.internal.compiler.parser;

public class ExpInitializer extends Initializer {

	public Expression exp;

	public ExpInitializer(Expression exp) {
		this.exp = exp;
		this.start = exp.start;
		this.length = exp.length;
	}
	
	@Override
	public ExpInitializer isExpInitializer() {
		return this;
	}
	
	@Override
	public Type inferType(Scope sc, SemanticContext context) {
	    exp = exp.semantic(sc, context);
	    exp = resolveProperties(sc, exp, context);
	    return exp.type;
	}
	
	public static Expression resolveProperties(Scope sc, Expression e,
			SemanticContext context) {
		if (e.type != null) {
			Type t = e.type.toBasetype(context);

			if (t.ty == TY.Tfunction) {
				e = new CallExp(e);
				e = e.semantic(sc, context);
			}

			/*
			 * Look for e being a lazy parameter; rewrite as delegate call
			 */
			else if (e.op == TOK.TOKvar) {
				VarExp ve = (VarExp) e;

				if ((ve.var.storage_class & STC.STClazy) != 0) {
					e = new CallExp(e);
					e = e.semantic(sc, context);
				}
			}

			else if (e.op == TOK.TOKdotexp) {
				e.error("expression has no value");
			}
		}
		return e;
	}
	
	@Override
	public Initializer semantic(Scope sc, Type t, SemanticContext context) {
	    exp = exp.semantic(sc, context);
		Type tb = t.toBasetype(context);

		/*
		 * Look for case of initializing a static array with a too-short string
		 * literal, such as: char[5] foo = "abc"; Allow this by doing an
		 * explicit cast, which will lengthen the string literal.
		 */
		if (exp.op == TOK.TOKstring && tb.ty == TY.Tsarray
				&& exp.type.ty == TY.Tsarray) {
			StringExp se = (StringExp) exp;

			if (se.committed == 0
					&& se.type.ty == TY.Tsarray
					&& ((TypeSArray) se.type).dim.toInteger(context).compareTo(
							((TypeSArray) t).dim.toInteger(context)) < 0) {
				exp = se.castTo(sc, t, context);
				// goto L1;
				exp = exp.optimize(Expression.WANTvalue
						| Expression.WANTinterpret);
				return this;
			}
		}

		// Look for the case of statically initializing an array
		// with a single member.
		if (tb.ty == TY.Tsarray
				&& !tb.next.equals(exp.type.toBasetype(context).next)
				&& exp.implicitConvTo(tb.next, context)) {
			t = tb.next;
		}

		exp = exp.implicitCastTo(sc, t);
		// L1:
		exp = exp.optimize(Expression.WANTvalue | Expression.WANTinterpret);
		return this;
	}
	
	@Override
	public int kind() {
		return EXP_INITIALIZER;
	}

}
