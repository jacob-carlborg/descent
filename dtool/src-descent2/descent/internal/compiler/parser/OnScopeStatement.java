package descent.internal.compiler.parser;


public class OnScopeStatement extends Statement {
	
	public TOK tok;
	public Statement statement;

	public OnScopeStatement(Loc loc, TOK tok, Statement statement) {
		super(loc);
		this.tok = tok;
		this.statement = statement;		
	}
	
	@Override
	public int getNodeType() {
		return ON_SCOPE_STATEMENT;
	}

}
