package descent.internal.compiler.parser;

public class BreakStatement extends Statement {

	public IdentifierExp ident;

	public BreakStatement(IdentifierExp ident) {
		this.ident = ident;		
	}
	
	@Override
	public int kind() {
		return BREAK_STATEMENT;
	}

}
