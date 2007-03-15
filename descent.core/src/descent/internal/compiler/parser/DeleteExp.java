package descent.internal.compiler.parser;

public class DeleteExp extends UnaExp {

	public DeleteExp(Expression e1) {
		super(TOK.TOKdelete, e1);
	}
	
	@Override
	public int kind() {
		return DELETE_EXP;
	}

}