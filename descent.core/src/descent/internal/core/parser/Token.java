package descent.internal.core.parser;

import java.math.BigInteger;


public class Token {
	
	public Token next;
	public int ptr; // The start position of the token
	public TOK value;
	
	public String string; // the string value of the token, if any
	public int len; // The length of the token
	public int postfix;
	
	public BigInteger numberValue; // Numeric value of the token, see if it is needed
	
	public Identifier ident;
	
	public Token() {
		
	}
	
	public Token(Token other) {
		this.next = other.next;
		this.ptr = other.ptr;
		this.value = other.value;
		this.string = other.string;
		this.len = other.len;
		this.postfix = other.postfix;
		this.numberValue = other.numberValue;
		this.ident = other.ident;
	}
	
	@Override
	public String toString() {
		return value.toString();
	}

}