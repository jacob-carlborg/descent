package descent.internal.compiler.parser;

public class UshrExp extends BinExp {

	public UshrExp(Expression e1, Expression e2) {
		super(TOK.TOKushr, e1, e2);
	}
	
	@Override
	public int getNodeType() {
		return USHR_EXP;
	}

}
