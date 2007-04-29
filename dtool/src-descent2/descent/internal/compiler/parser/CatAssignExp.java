package descent.internal.compiler.parser;

public class CatAssignExp extends BinExp {

	public CatAssignExp(Loc loc, Expression e1, Expression e2) {
		super(loc, TOK.TOKcatass, e1, e2);
	}
	
	@Override
	public int getNodeType() {
		return CAT_ASSIGN_EXP;
	}

}
