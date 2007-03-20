package descent.internal.compiler.parser;

public class UAddExp extends UnaExp {

	public UAddExp(Expression e1) {
		super(TOK.TOKuadd, e1);
	}
	
	@Override
	public int getNodeType() {
		return UADD_EXP;
	}

}
