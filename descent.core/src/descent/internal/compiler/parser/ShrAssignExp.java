package descent.internal.compiler.parser;

public class ShrAssignExp extends BinExp {

	public ShrAssignExp(Loc loc, Expression e1, Expression e2) {
		super(loc, TOK.TOKshrass, e1, e2);
	}
	
	@Override
	public int getNodeType() {
		return SHR_ASSIGN_EXP;
	}

}
