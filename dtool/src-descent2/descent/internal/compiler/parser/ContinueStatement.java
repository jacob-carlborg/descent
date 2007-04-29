package descent.internal.compiler.parser;

public class ContinueStatement extends Statement {

	public IdentifierExp ident;

	public ContinueStatement(Loc loc, IdentifierExp ident) {
		super(loc);
		this.ident = ident;		
	}
	
	@Override
	public int getNodeType() {
		return CONTINUE_STATEMENT;
	}

}
