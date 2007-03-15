package descent.internal.compiler.parser;

/*
 * Identifier + IdentifierExp
 */
public class IdentifierExp extends Expression {
	
	public Identifier ident;
	
	public IdentifierExp() {
		super(TOK.TOKidentifier);
	}
	
	public IdentifierExp(Identifier ident) {
		this();
		this.ident = ident;
	}
	
	public IdentifierExp(Token token) {
		this(token.ident);
		this.start = token.ptr;
		this.length = token.len;
	}
	
	@Override
	public int kind() {
		return IDENTIFIER_EXP;
	}
	
	@Override
	public String toString() {
		return ident.string;
	}

}
