package descent.internal.compiler.parser;


public class Identifier {
	
	public int startPosition;
	public int length;
	public int lineNumber;
	public String string;
	public TOK value;
	
	public Identifier(String string) {
		this(string, TOK.TOKidentifier);
	}
	
	public Identifier(String string, TOK value) {
		this.string = string;
		this.value = value;
	}
	
	public Identifier(Token token) {
		this.string = token.ident.string;
		this.value = token.value;
		this.startPosition = token.ptr;
		this.length = token.len;
		this.lineNumber = token.lineNumber;
	}
	
	public String getIdentifier() {
		return string;
	}
	
	@Override
	public String toString() {
		return string;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Identifier)) {
			return false;
		}
		
		return string.equals(((Identifier) o).string);
	}

}
