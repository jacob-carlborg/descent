package descent.internal.compiler.parser;

public class AndAssignExp extends BinExp {

	public AndAssignExp(Loc loc, Expression e1, Expression e2) {
		super(loc, TOK.TOKandass, e1, e2);
	}
	
	@Override
	public int getNodeType() {
		return AND_ASSIGN_EXP;
	}

}
