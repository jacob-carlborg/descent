package descent.internal.compiler.parser;

public class ShrAssignExp extends BinExp {

	public ShrAssignExp(Expression e1, Expression e2) {
		super(TOK.TOKshrass, e1, e2);
	}
	
	@Override
	public int getNodeType() {
		return SHR_ASSIGN_EXP;
	}

}
