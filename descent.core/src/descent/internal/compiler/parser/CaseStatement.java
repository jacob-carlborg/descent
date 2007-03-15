package descent.internal.compiler.parser;

public class CaseStatement extends Statement {

	public Expression exp;
	public Statement statement;

	public CaseStatement(Expression exp, Statement s) {
		this.exp = exp;
		this.statement = s;		
	}
	
	@Override
	public int kind() {
		return CASE_STATEMENT;
	}

}
