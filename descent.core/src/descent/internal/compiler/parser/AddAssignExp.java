package descent.internal.compiler.parser;

public class AddAssignExp extends BinExp {

	public AddAssignExp(Loc loc, Expression e1, Expression e2) {
		super(loc, TOK.TOKaddass, e1, e2);
	}
	
	@Override
	public int getNodeType() {
		return ADD_ASSIGN_EXP;
	}

}
