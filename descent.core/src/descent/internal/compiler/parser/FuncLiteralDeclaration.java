package descent.internal.compiler.parser;

public class FuncLiteralDeclaration extends FuncDeclaration {
	
	public TOK tok; // TOKfunction or TOKdelegate
	
	public FuncLiteralDeclaration(Type type, TOK tok, ForeachStatement fes) {
		super(null, type);
		this.tok = tok;
	}
	
	@Override
	public int kind() {
		return FUNC_LITERAL_DECLARATION;
	}

}
