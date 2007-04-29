package descent.internal.compiler.parser;

public class DollarExp extends Expression {

	public DollarExp(Loc loc) {
		super(loc, TOK.TOKdollar);
	}
	
	@Override
	public int getNodeType() {
		return DOLLAR_EXP;
	}

}
