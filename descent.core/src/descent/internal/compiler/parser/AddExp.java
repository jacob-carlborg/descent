package descent.internal.compiler.parser;

public class AddExp extends BinExp {

	public AddExp(Expression e1, Expression e2) {
		super(TOK.TOKadd, e1, e2);
	}
	
	@Override
	public int kind() {
		return ADD_EXP;
	}

}
