package descent.internal.compiler.parser;

public class BreakStatement extends Statement {

	public IdentifierExp ident;

	public BreakStatement(Loc loc, IdentifierExp ident) {
		super(loc);
		this.ident = ident;		
	}
	
	@Override
	public int getNodeType() {
		return BREAK_STATEMENT;
	}

}
