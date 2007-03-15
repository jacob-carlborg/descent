package descent.internal.compiler.parser;

public class PtrExp extends UnaExp {

	public PtrExp(Expression e1) {
		super(TOK.TOKstar, e1);
	}
	
	@Override
	public int kind() {
		return PTR_EXP;
	}

}
