package descent.internal.compiler.parser;

public class CommaExp extends BinExp {

	public CommaExp(Expression e1, Expression e2) {
		super(TOK.TOKcomma, e1, e2);
	}
	
	@Override
	public int getNodeType() {
		return COMMA_EXP;
	}

}
