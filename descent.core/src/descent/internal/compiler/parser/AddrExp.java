package descent.internal.compiler.parser;

public class AddrExp extends UnaExp {

	public AddrExp(Expression e) {
		super(TOK.TOKaddress, e);
	}
	
	@Override
	public int kind() {
		return ADDR_EXP;
	}

}
