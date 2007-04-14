package descent.internal.compiler.parser;

public class OrExp extends BinExp {

	public OrExp(Loc loc, Expression e1, Expression e2) {
		super(loc, TOK.TOKor, e1, e2);
	}
	
	@Override
	public int getNodeType() {
		return OR_EXP;
	}

}
