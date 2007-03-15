package descent.internal.compiler.parser;

public class DollarExp extends Expression {

	public DollarExp() {
		super(TOK.TOKdollar);
	}
	
	@Override
	public int kind() {
		return DOLLAR_EXP;
	}

}
