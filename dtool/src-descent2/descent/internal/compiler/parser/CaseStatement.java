package descent.internal.compiler.parser;

public class CaseStatement extends Statement {

	public Expression exp;
	public Statement statement;

	public CaseStatement(Loc loc, Expression exp, Statement s) {
		super(loc);
		this.exp = exp;
		this.statement = s;		
	}
	
	@Override
	public int getNodeType() {
		return CASE_STATEMENT;
	}

}
