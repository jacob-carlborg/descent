package descent.internal.compiler.parser;

public class ModAssignExp extends BinExp {

	public ModAssignExp(Expression e1, Expression e2) {
		super(TOK.TOKmodass, e1, e2);
	}
	
	@Override
	public int kind() {
		return MOD_ASSIGN_EXP;
	}

}
