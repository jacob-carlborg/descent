package descent.internal.compiler.parser;

public class IndexExp extends BinExp {
	
	public VarDeclaration lengthVar;
	public int modifiable;

	public IndexExp(Expression e1, Expression e2) {
		super(TOK.TOKindex, e1, e2);
	}

	@Override
	public int getNodeType() {
		return 0;
	}

}
