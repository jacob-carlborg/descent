package descent.internal.compiler.parser;

public class DotExp extends BinExp {

	public DotExp(Loc loc, Expression e1, Expression e2) {
		super(loc, TOK.TOKdot, e1, e2);
	}

	@Override
	public int getNodeType() {
		return 0;
	}

}
