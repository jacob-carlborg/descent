package descent.internal.compiler.parser;

public class GotoStatement extends Statement {

	public IdentifierExp ident;
	public LabelDsymbol label;
	public TryFinallyStatement tf;

	public GotoStatement(IdentifierExp ident) {
		this.ident = ident;		
	}
	
	@Override
	public int getNodeType() {
		return GOTO_STATEMENT;
	}

}
