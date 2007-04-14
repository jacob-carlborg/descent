package descent.internal.compiler.parser;

public class IndexExp extends BinExp {
	
	public VarDeclaration lengthVar;
	public int modifiable;

	public IndexExp(Loc loc, Expression e1, Expression e2) {
		super(loc, TOK.TOKindex, e1, e2);
	}

	@Override
	public int getNodeType() {
		return 0;
	}

}
