package descent.internal.compiler.parser;


public class IdentityExp extends BinExp {

	public IdentityExp(Loc loc, TOK op, Expression e1, Expression e2) {
		super(loc, op, e1, e2);
	}
	
	@Override
	public int getNodeType() {
		return IDENTITY_EXP;
	}

}
