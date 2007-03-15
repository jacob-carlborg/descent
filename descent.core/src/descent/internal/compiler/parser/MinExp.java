package descent.internal.compiler.parser;

public class MinExp extends BinExp {

	public MinExp(Expression e1, Expression e2) {
		super(TOK.TOKmin, e1, e2);
	}
	
	@Override
	public int kind() {
		return MIN_EXP;
	}

}
