package descent.internal.compiler.parser;

public class AndExp extends BinExp {

	public AndExp(Loc loc, Expression e1, Expression e2) {
		super(loc, TOK.TOKand, e1, e2);
	}
	
	@Override
	public int getNodeType() {
		return AND_EXP;
	}

}
