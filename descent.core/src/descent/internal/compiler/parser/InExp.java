package descent.internal.compiler.parser;

public class InExp extends BinExp {

	public InExp(Loc loc, Expression e1, Expression e2) {
		super(loc, TOK.TOKin, e1, e2);
	}
	
	@Override
	public int getNodeType() {
		return IN_EXP;
	}

}
