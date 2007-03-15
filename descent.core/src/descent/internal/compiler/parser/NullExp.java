package descent.internal.compiler.parser;

public class NullExp extends Expression {
	
	public NullExp() {
		super(TOK.TOKnull);
	}
	
	@Override
	public int kind() {
		return NULL_EXP;
	}

}
