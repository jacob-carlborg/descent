package descent.internal.compiler.parser;

public class DotVarExp extends UnaExp {
	
	public Declaration var;

	public DotVarExp(Expression e, Declaration var) {
		super(TOK.TOKdotvar, e);
		this.var = var;
	}

	@Override
	public int getNodeType() {
		return DOT_VAR_EXP;
	}

}
