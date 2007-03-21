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
	
	public boolean dyncast() {
		return Identifier.DYNCAST_IDENTIFIER;
	}
	
	@Override
	public int getNodeType() {
		return IDENTIFIER_EXP;
	}
	
	@Override
	public String toString() {
		return ident.toString();
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof IdentifierExp)) {
			return false;
		}
		
		IdentifierExp i = (IdentifierExp) o;
		return ident.equals(i.ident);
	}

}
