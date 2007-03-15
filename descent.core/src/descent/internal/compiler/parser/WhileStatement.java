package descent.internal.compiler.parser;

public class WhileStatement extends Statement {
	
	public Expression condition;
	public Statement body;
	
	public WhileStatement(Expression c, Statement b) {
		this.condition = c;
		this.body = b;
	}
	
	@Override
	public int kind() {
		return WHILE_STATEMENT;
	}

}
