package descent.internal.compiler.parser;

public class ShlAssignExp extends BinExp {

	public ShlAssignExp(Expression e1, Expression e2) {
		super(TOK.TOKshlass, e1, e2);
	}
	
	@Override
	public int kind() {
		return SHL_ASSIGN_EXP;
	}

}
