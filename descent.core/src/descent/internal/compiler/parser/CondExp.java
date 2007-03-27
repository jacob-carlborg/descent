package descent.internal.compiler.parser;

import static descent.internal.compiler.parser.TOK.*;

public class CondExp extends BinExp {

	public Expression econd;

	public CondExp(Expression econd, Expression e1, Expression e2) {
		super(TOK.TOKquestion, e1, e2);
		this.econd = econd;
	}
	
	@Override
	public Expression castTo(Scope sc, Type t, SemanticContext context) {
		Expression e = this;

		if (type != t) {
			if (true || e1.op == TOKstring || e2.op == TOKstring) {
				e = new CondExp(econd, e1.castTo(sc, t, context), e2.castTo(sc,
						t, context));
				e.type = t;
			} else
				e = super.castTo(sc, t, context);
		}
		return e;
	}

	@Override
	public int getNodeType() {
		return COND_EXP;
	}

	@Override
	public MATCH implicitConvTo(Type t, SemanticContext context) {
		MATCH m1;
		MATCH m2;

		m1 = e1.implicitConvTo(t, context);
		m2 = e2.implicitConvTo(t, context);

		// Pick the worst match
		return (m1.ordinal() < m2.ordinal()) ? m1 : m2;
	}

}
