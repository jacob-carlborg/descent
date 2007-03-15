package descent.internal.compiler.parser;

public class OrOrExp extends BinExp {

	public OrOrExp(Expression e1, Expression e2) {
		super(TOK.TOKoror, e1, e2);
	}
	
	@Override
	public int kind() {
		return OR_OR_EXP;
	}

}
