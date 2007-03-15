package descent.internal.compiler.parser;

public class CatAssignExp extends BinExp {

	public CatAssignExp(Expression e1, Expression e2) {
		super(TOK.TOKcatass, e1, e2);
	}
	
	@Override
	public int kind() {
		return CAT_ASSIGN_EXP;
	}

}
