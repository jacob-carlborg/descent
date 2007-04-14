package descent.internal.compiler.parser;

public class RemoveExp extends BinExp {

	public RemoveExp(Loc loc, Expression e1, Expression e2) {
		super(loc, TOK.TOKremove, e1, e2);
	}

	@Override
	public int getNodeType() {
		return 0;
	}

}
