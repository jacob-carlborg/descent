package descent.internal.core.parser;


public class Identifier {
	
	public int startPosition;
	public int length;
	public int lineNumber;
	public String string;
	public TOK value;
	
	private static int instances = 0;
	
	public Identifier(String string, TOK value) {
		this.string = string;
		this.value = value;
		//System.out.println("Identifier 1: " + ++instances);
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

}
