package descent.internal.compiler.parser;

public class UshrAssignExp extends BinExp {

	public UshrAssignExp(Expression e1, Expression e2) {
		super(TOK.TOKushrass, e1, e2);
	}
	
	@Override
	public int getNodeType() {
		return USHR_ASSIGN_EXP;
	}

}
