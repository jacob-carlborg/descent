package descent.internal.compiler.parser;

public class CommaExp extends BinExp {

	public CommaExp(Expression e1, Expression e2) {
		super(TOK.TOKcomma, e1, e2);
	}
	
	@Override
	public boolean isBool(boolean result) {
		return e2.isBool(result);
	}
	
	@Override
	public int getNodeType() {
		return COMMA_EXP;
	}

}
