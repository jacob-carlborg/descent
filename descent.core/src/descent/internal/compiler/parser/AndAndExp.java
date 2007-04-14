package descent.internal.compiler.parser;

public class AndAndExp extends BinExp {

	public AndAndExp(Loc loc, Expression e1, Expression e2) {
		super(loc, TOK.TOKandand, e1, e2);
	}
	
	@Override
	public int getNodeType() {
		return AND_AND_EXP;
	}

}
