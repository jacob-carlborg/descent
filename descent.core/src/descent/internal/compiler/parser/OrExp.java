package descent.internal.compiler.parser;

public class OrExp extends BinExp {

	public OrExp(Expression e1, Expression e2) {
		super(TOK.TOKor, e1, e2);
	}
	
	@Override
	public int kind() {
		return OR_EXP;
	}

}
