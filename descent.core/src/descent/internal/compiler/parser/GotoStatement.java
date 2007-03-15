package descent.internal.compiler.parser;

public class GotoStatement extends Statement {

	public IdentifierExp ident;

	public GotoStatement(IdentifierExp ident) {
		this.ident = ident;		
	}
	
	@Override
	public int kind() {
		return GOTO_STATEMENT;
	}

}
