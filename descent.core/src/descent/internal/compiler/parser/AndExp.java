package descent.internal.compiler.parser;

public class AndExp extends BinExp {

	public AndExp(Expression e1, Expression e2) {
		super(TOK.TOKand, e1, e2);
	}
	
	@Override
	public int getNodeType() {
		return AND_EXP;
	}

}
