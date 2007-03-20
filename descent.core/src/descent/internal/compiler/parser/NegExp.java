package descent.internal.compiler.parser;

public class NegExp extends UnaExp {

	public NegExp(Expression e1) {
		super(TOK.TOKneg, e1);
	}
	
	@Override
	public int getNodeType() {
		return NEG_EXP;
	}

}
