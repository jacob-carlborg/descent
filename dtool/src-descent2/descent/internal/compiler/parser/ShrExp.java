package descent.internal.compiler.parser;

public class ShrExp extends BinExp {

	public ShrExp(Loc loc, Expression e1, Expression e2) {
		super(loc, TOK.TOKshr, e1, e2);
	}
	
	@Override
	public int getNodeType() {
		return SHR_EXP;
	}

}
