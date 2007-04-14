package descent.internal.compiler.parser;

public class MulAssignExp extends BinExp {

	public MulAssignExp(Loc loc, Expression e1, Expression e2) {
		super(loc, TOK.TOKmulass, e1, e2);
	}
	
	@Override
	public int getNodeType() {
		return MUL_ASSIGN_EXP;
	}

}
