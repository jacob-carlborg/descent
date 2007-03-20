package descent.internal.compiler.parser;

public class ModExp extends BinExp {

	public ModExp(Expression e1, Expression e2) {
		super(TOK.TOKmod, e1, e2);
	}
	
	@Override
	public int getNodeType() {
		return MOD_EXP;
	}

}
