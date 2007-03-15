package descent.internal.compiler.parser;

public class LabelStatement extends Statement {
	
	public IdentifierExp ident;
	public Statement statement;

	public LabelStatement(IdentifierExp ident, Statement statement) {
		this.ident = ident;
		this.statement = statement;
		this.start = ident.start;
		this.length = statement.start + statement.length - ident.start;
	}
	
	@Override
	public int kind() {
		return LABEL_STATEMENT;
	}
	
	
}