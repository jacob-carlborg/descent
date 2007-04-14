package descent.internal.compiler.parser;

public class DivAssignExp extends BinExp {

	public DivAssignExp(Loc loc, Expression e1, Expression e2) {
		super(loc, TOK.TOKdivass, e1, e2);
	}
	
	@Override
	public int getNodeType() {
		return DIV_ASSIGN_EXP;
	}

}
