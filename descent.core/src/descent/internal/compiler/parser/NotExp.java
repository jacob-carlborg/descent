package descent.internal.compiler.parser;

public class NotExp extends UnaExp {

	public NotExp(Expression e1) {
		super(TOK.TOKnot, e1);
	}
	
	@Override
	public int getNodeType() {
		return NOT_EXP;
	}

}
