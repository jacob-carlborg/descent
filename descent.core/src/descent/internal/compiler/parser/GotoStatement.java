package descent.internal.compiler.parser;

public class GotoStatement extends Statement {

	public IdentifierExp ident;
	public LabelDsymbol label;
	public TryFinallyStatement tf;

	public GotoStatement(Loc loc, IdentifierExp ident) {
		super(loc);
		this.ident = ident;		
	}
	
	@Override
	public int getNodeType() {
		return GOTO_STATEMENT;
	}

}
