package descent.internal.compiler.parser;

public class DoStatement extends Statement {
	
	public Expression condition;
	public Statement body;
	
	public DoStatement(Statement b, Expression c) {
		this.condition = c;
		this.body = b;
	}
	
	@Override
	public int getNodeType() {
		return DO_STATEMENT;
	}

}
