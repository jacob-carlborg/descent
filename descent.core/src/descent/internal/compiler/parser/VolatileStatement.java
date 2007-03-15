package descent.internal.compiler.parser;

public class VolatileStatement extends Statement {
	
	public Statement statement;
	
	public VolatileStatement(Statement statement) {
		this.statement = statement;
	}
	
	@Override
	public int kind() {
		return VOLATILE_STATEMENT;
	}

}
