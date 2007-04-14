package descent.internal.compiler.parser;

public class VolatileStatement extends Statement {
	
	public Statement statement;
	
	public VolatileStatement(Loc loc, Statement statement) {
		super(loc);
		this.statement = statement;
	}
	
	@Override
	public int getNodeType() {
		return VOLATILE_STATEMENT;
	}

}
