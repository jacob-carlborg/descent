package descent.internal.compiler.parser;

public class DelegateExp extends UnaExp {
	
	public FuncDeclaration func;

	public DelegateExp(Expression e, FuncDeclaration func) {
		super(TOK.TOKdelegate, e);
		this.func = func;
	}

	@Override
	public int getNodeType() {
		return DELEGATE_EXP;
	}

}
