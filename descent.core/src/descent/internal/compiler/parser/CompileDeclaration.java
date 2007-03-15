package descent.internal.compiler.parser;

public class CompileDeclaration extends AttribDeclaration {
	
	public Expression exp;
	
	public CompileDeclaration(Expression exp) {
		super(null);
		this.exp = exp;
	}
	
	@Override
	public int kind() {
		return COMPILE_DECLARATION;
	}

}
