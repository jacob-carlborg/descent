package descent.internal.compiler.parser;

public class SwitchStatement extends Statement {

	public Expression condition;
	public Statement body;

	public SwitchStatement(Expression c, Statement b) {
		this.condition = c;
		this.body = b;		
	}
	
	@Override
	public int getNodeType() {
		return SWITCH_STATEMENT;
	}

}
