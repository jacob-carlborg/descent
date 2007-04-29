package descent.internal.compiler.parser;

public class OrAssignExp extends BinExp {

	public OrAssignExp(Loc loc, Expression e1, Expression e2) {
		super(loc, TOK.TOKorass, e1, e2);
	}
	
	@Override
	public int getNodeType() {
		return OR_ASSIGN_EXP;
	}

}
