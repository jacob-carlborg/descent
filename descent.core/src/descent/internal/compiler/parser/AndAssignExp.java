package descent.internal.compiler.parser;

public class AndAssignExp extends BinExp {

	public AndAssignExp(Expression e1, Expression e2) {
		super(TOK.TOKandass, e1, e2);
	}
	
	@Override
	public int getNodeType() {
		return AND_ASSIGN_EXP;
	}

}
