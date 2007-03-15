package descent.internal.compiler.parser;


public class EqualExp extends BinExp {

	public EqualExp(TOK op, Expression e1, Expression e2) {
		super(op, e1, e2);
	}
	
	@Override
	public int kind() {
		return EQUAL_EXP;
	}

}
