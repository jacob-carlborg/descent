package descent.internal.compiler.parser;

public class FuncLiteralDeclaration extends FuncDeclaration {
	
	public TOK tok; // TOKfunction or TOKdelegate
	
	public FuncLiteralDeclaration(Type type, TOK tok, ForeachStatement fes) {
		super(null, STC.STCundefined, type);
		this.tok = tok;
	}
	
	@Override
	public FuncLiteralDeclaration isFuncLiteralDeclaration() {
		return this;
	}
	
	@Override
	public int getNodeType() {
		return FUNC_LITERAL_DECLARATION;
	}

}
