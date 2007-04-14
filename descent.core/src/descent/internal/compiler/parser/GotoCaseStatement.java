package descent.internal.compiler.parser;

public class GotoCaseStatement extends Statement {

	public Expression exp;

	public GotoCaseStatement(Loc loc, Expression exp) {
		super(loc);
		this.exp = exp;		
	}
	
	@Override
	public int getNodeType() {
		return GOTO_CASE_STATEMENT;
	}

}
