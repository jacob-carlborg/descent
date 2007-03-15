package descent.internal.compiler.parser;

public class ContinueStatement extends Statement {

	public IdentifierExp ident;

	public ContinueStatement(IdentifierExp ident) {
		this.ident = ident;		
	}
	
	@Override
	public int kind() {
		return CONTINUE_STATEMENT;
	}

}
