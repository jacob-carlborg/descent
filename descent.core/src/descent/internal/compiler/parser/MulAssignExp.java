package descent.internal.compiler.parser;

public class MulAssignExp extends BinExp {

	public MulAssignExp(Expression e1, Expression e2) {
		super(TOK.TOKmulass, e1, e2);
	}
	
	@Override
	public int getNodeType() {
		return MUL_ASSIGN_EXP;
	}

}
