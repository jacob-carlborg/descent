package descent.internal.compiler.parser;

public class GotoCaseStatement extends Statement {

	public Expression exp;

	public GotoCaseStatement(Expression exp) {
		this.exp = exp;		
	}
	
	@Override
	public int kind() {
		return GOTO_CASE_STATEMENT;
	}

}