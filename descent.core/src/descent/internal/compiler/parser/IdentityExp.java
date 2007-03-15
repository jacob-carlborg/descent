package descent.internal.compiler.parser;


public class IdentityExp extends BinExp {

	public IdentityExp(TOK op, Expression e1, Expression e2) {
		super(op, e1, e2);
	}
	
	@Override
	public int kind() {
		return IDENTITY_EXP;
	}

}
