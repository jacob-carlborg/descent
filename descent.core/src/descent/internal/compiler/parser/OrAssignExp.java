package descent.internal.compiler.parser;

public class OrAssignExp extends BinExp {

	public OrAssignExp(Expression e1, Expression e2) {
		super(TOK.TOKorass, e1, e2);
	}
	
	@Override
	public int kind() {
		return OR_ASSIGN_EXP;
	}

}
