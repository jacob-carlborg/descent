package descent.internal.compiler.parser;

public class AssignExp extends BinExp {

	public AssignExp(Expression e1, Expression e2) {
		super(TOK.TOKassign, e1, e2);
	}
	
	@Override
	public int getNodeType() {
		return ASSIGN_EXP;
	}

}
