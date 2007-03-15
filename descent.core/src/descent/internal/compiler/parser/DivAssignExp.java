package descent.internal.compiler.parser;

public class DivAssignExp extends BinExp {

	public DivAssignExp(Expression e1, Expression e2) {
		super(TOK.TOKdivass, e1, e2);
	}
	
	@Override
	public int kind() {
		return DIV_ASSIGN_EXP;
	}

}
