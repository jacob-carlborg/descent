package descent.internal.compiler.parser;


public class EqualExp extends BinExp {

	public EqualExp(Loc loc, TOK op, Expression e1, Expression e2) {
		super(loc, op, e1, e2);
	}
	
	@Override
	public int getNodeType() {
		return EQUAL_EXP;
	}

}
