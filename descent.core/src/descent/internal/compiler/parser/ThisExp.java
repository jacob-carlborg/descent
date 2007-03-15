package descent.internal.compiler.parser;

public class ThisExp extends Expression {

	public ThisExp() {
		super(TOK.TOKthis);
	}
		
	@Override
	public int kind() {
		return THIS_EXP;
	}

}
