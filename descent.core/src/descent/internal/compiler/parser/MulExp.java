package descent.internal.compiler.parser;

public class MulExp extends BinExp {

	public MulExp(Loc loc, Expression e1, Expression e2) {
		super(loc, TOK.TOKmul, e1, e2);
	}
	
	@Override
	public int getNodeType() {
		return MUL_EXP;
	}

}
