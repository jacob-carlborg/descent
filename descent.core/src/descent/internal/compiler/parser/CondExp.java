package descent.internal.compiler.parser;

public class CondExp extends BinExp {

	public Expression econd;

	public CondExp(Expression econd, Expression e1, Expression e2) {
		super(TOK.TOKquestion, e1, e2);
		this.econd = econd;
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
