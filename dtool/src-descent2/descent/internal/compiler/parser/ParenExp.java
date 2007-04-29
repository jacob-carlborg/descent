package descent.internal.compiler.parser;

public class ParenExp extends UnaExp {

	public ParenExp(Loc loc, Expression e) {
		super(loc, TOK.TOKlparen, e);
	}
	
	@Override
	public int getNodeType() {
		return PAREN_EXP;
	}

}
