package descent.internal.compiler.parser;

public class DefaultStatement extends Statement {

	public Statement statement;

	public DefaultStatement(Statement s) {
		this.statement = s;		
	}
	
	@Override
	public int kind() {
		return DEFAULT_STATEMENT;
	}

}
