package descent.internal.compiler.parser;

public class FuncExp extends Expression {

	public FuncLiteralDeclaration fd;

	public FuncExp(FuncLiteralDeclaration fd) {
		super(TOK.TOKfunction);
		this.fd = fd;
	}
	
	@Override
	public int kind() {
		return FUNC_EXP;
	}

}