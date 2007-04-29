package descent.internal.compiler.parser;

public class AddExp extends BinExp {

	public AddExp(Loc loc, Expression e1, Expression e2) {
		super(loc, TOK.TOKadd, e1, e2);
	}
	
	@Override
	public int getNodeType() {
		return ADD_EXP;
	}

}
