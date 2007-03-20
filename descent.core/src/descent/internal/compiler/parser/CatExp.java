package descent.internal.compiler.parser;

public class CatExp extends BinExp {

	public CatExp(Expression e1, Expression e2) {
		super(TOK.TOKtilde, e1, e2);
	}
	
	@Override
	public int getNodeType() {
		return CAT_EXP;
	}

}
