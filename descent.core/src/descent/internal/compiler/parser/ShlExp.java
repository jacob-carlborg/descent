package descent.internal.compiler.parser;

public class ShlExp extends BinExp {

	public ShlExp(Loc loc, Expression e1, Expression e2) {
		super(loc, TOK.TOKshl, e1, e2);
	}
	
	@Override
	public int getNodeType() {
		return SHL_EXP;
	}

}
