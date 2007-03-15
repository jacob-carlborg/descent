package descent.internal.compiler.parser;

public class ReturnStatement extends Statement {

	public Expression exp;

	public ReturnStatement(Expression exp) {
		this.exp = exp;		
	}	
	
	@Override
	public int kind() {
		return RETURN_STATEMENT;
	}

}
