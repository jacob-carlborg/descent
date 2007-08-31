package descent.internal.compiler.parser;

import descent.core.compiler.CharOperation;


public class Identifier {
	
	public int startPosition;
	public int length;
	public int lineNumber;
	public char[] string;
	public TOK value;
	
	public Identifier(char[] string) {
		this(string, TOK.TOKidentifier);
	}
	
	public Identifier(char[] string, TOK value) {
		this.string = string;
		this.value = value;
	}
	
	public Identifier(Token token) {
		this.string = token.string;
		this.value = token.value;
		this.startPosition = token.ptr;
		this.length = token.len;
		this.lineNumber = token.lineNumber;
	}
	
	public char[] getIdentifier() {
		return string;
	}
	
	public String toChars() {
		return new String(string).intern();
	}
	
	@Override
	public String toString() {
		return toChars();
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Identifier)) {
			return false;
		}
		
		return CharOperation.equals(string, ((Identifier) o).string);
	}

	public String toHChars2() {
		// TODO semantic
		return null;
	}

}
