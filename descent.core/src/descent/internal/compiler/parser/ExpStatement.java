package descent.internal.compiler.parser;

public class ExpStatement extends Statement {
	
	public Expression exp;

	public ExpStatement(Expression exp) {
		this.exp = exp;		
	}
	
	@Override
	public int kind() {
		return EXP_STATEMENT;
	}

}