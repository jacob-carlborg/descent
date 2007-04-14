package descent.internal.compiler.parser;

public class MinAssignExp extends BinExp {

	public MinAssignExp(Loc loc, Expression e1, Expression e2) {
		super(loc, TOK.TOKminass, e1, e2);
	}
	
	@Override
	public int getNodeType() {
		return MIN_ASSIGN_EXP;
	}

}
