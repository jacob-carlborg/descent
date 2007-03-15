package descent.internal.compiler.parser;

public class ParenExp extends UnaExp {

	public ParenExp(Expression e) {
		super(TOK.TOKlparen, e);
	}
	
	@Override
	public int kind() {
		return PAREN_EXP;
	}

}
