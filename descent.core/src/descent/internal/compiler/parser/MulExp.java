package descent.internal.compiler.parser;

public class MulExp extends BinExp {

	public MulExp(Expression e1, Expression e2) {
		super(TOK.TOKmul, e1, e2);
	}
	
	@Override
	public int kind() {
		return MUL_EXP;
	}

}
