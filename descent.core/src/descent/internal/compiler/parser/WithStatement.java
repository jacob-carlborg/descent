package descent.internal.compiler.parser;

public class WithStatement extends Statement {

	public Expression exp;
	public Statement body;

	public WithStatement(Expression exp, Statement body) {
		this.exp = exp;
		this.body = body;		
	}
	
	@Override
	public int kind() {
		return WITH_STATEMENT;
	}

}
