package descent.internal.compiler.parser;

public class AssertExp extends UnaExp {

	public Expression msg;

	public AssertExp(Expression e, Expression msg) {
		super(TOK.TOKassert, e);
		this.msg = msg;
	}
	
	@Override
	public int getNodeType() {
		return ASSERT_EXP;
	}

}
