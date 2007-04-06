package descent.internal.compiler.parser;

public class DotTypeExp extends UnaExp {
	
	public Dsymbol sym;

	public DotTypeExp(Expression e, Dsymbol s) {
		super(TOK.TOKdottype, e);
		this.sym = s;
		this.type = s.getType();
	}

	@Override
	public int getNodeType() {
		return 0;
	}

}
