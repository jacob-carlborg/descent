package descent.internal.compiler.parser;

public class ShrExp extends BinExp {

	public ShrExp(Expression e1, Expression e2) {
		super(TOK.TOKshr, e1, e2);
	}
	
	@Override
	public int getNodeType() {
		return SHR_EXP;
	}

}
