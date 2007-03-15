package descent.internal.compiler.parser;

public class CastExp extends UnaExp {
	
	public Type to;

	public CastExp(Expression e1, Type t) {
		super(TOK.TOKcast, e1);
		this.to = t;
	}
	
	@Override
	public int kind() {
		return CAST_EXP;
	}

}
