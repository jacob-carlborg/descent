package descent.internal.compiler.parser;

public class MinExp extends BinExp {

	public MinExp(Loc loc, Expression e1, Expression e2) {
		super(loc, TOK.TOKmin, e1, e2);
	}
	
	@Override
	public int getNodeType() {
		return MIN_EXP;
	}

}
