package descent.internal.compiler.parser;

public class XorExp extends BinExp {

	public XorExp(Expression e1, Expression e2) {
		super(TOK.TOKxor, e1, e2);
	}
	
	@Override
	public int kind() {
		return XOR_EXP;
	}

}
