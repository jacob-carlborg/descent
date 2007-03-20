package descent.internal.compiler.parser;

public class ForStatement extends Statement {
	
	public final Statement init;
	public final Expression condition;
	public final Expression increment;
	public final Statement body;

	public ForStatement(Statement init, Expression condition, Expression increment, Statement body) {
		this.init = init;
		this.condition = condition;
		this.increment = increment;
		this.body = body;		
	}
	
	@Override
	public int getNodeType() {
		return FOR_STATEMENT;
	}

}
