package descent.internal.compiler.parser;

public class ShlExp extends BinExp {

	public ShlExp(Expression e1, Expression e2) {
		super(TOK.TOKshl, e1, e2);
	}
	
	@Override
	public int kind() {
		return SHL_EXP;
	}

}
