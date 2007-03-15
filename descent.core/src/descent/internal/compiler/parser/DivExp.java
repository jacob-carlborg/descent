package descent.internal.compiler.parser;

public class DivExp extends BinExp {

	public DivExp(Expression e1, Expression e2) {
		super(TOK.TOKdiv, e1, e2);
	}
	
	@Override
	public int kind() {
		return DIV_EXP;
	}

}
