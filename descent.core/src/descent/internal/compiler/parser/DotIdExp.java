package descent.internal.compiler.parser;

public class DotIdExp extends UnaExp {

	public IdentifierExp ident;

	public DotIdExp(Expression e, IdentifierExp id) {
		super(TOK.TOKdot, e);
		this.ident = id;
	}
	
	@Override
	public int getNodeType() {
		return DOT_ID_EXP;
	}

}
