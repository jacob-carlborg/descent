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

}
