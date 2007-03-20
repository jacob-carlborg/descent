package descent.internal.compiler.parser;

public class CompileExp extends UnaExp {

	public CompileExp(Expression e) {
		super(TOK.TOKmixin, e);
	}
	
	@Override
	public int getNodeType() {
		return COMPILE_EXP;
	}

}
