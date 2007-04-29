package descent.internal.compiler.parser;

public class ModAssignExp extends BinExp {

	public ModAssignExp(Loc loc, Expression e1, Expression e2) {
		super(loc, TOK.TOKmodass, e1, e2);
	}
	
	@Override
	public int getNodeType() {
		return MOD_ASSIGN_EXP;
	}

}
