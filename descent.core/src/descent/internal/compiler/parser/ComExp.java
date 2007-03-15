package descent.internal.compiler.parser;

public class ComExp extends UnaExp {

	public ComExp(Expression e1) {
		super(TOK.TOKtilde, e1);
	}
	
	@Override
	public int kind() {
		return COM_EXP;
	}

}
