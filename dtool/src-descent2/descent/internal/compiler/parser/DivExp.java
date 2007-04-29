package descent.internal.compiler.parser;

public class DivExp extends BinExp {

	public DivExp(Loc loc, Expression e1, Expression e2) {
		super(loc, TOK.TOKdiv, e1, e2);
	}
	
	@Override
	public int getNodeType() {
		return DIV_EXP;
	}

}
