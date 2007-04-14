package descent.internal.compiler.parser;

public class DefaultStatement extends Statement {

	public Statement statement;

	public DefaultStatement(Loc loc, Statement s) {
		super(loc);
		this.statement = s;		
	}
	
	@Override
	public int getNodeType() {
		return DEFAULT_STATEMENT;
	}

}
