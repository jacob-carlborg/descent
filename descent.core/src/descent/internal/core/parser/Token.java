package descent.internal.core.parser;

import descent.core.dom.DDocComment;


public class Token {
	
	public Token next;
	public int ptr; // The start position of the token
	public TOK value;
	public String string; // the string value of the token, if any
	public int len; // The length of the token
	public int postfix;
	//public BigInteger numberValue; // Numeric value of the token, see if it is needed
	public Identifier ident;
	public int lineNumber;
	public DDocComment leadingComment;
	
	//private static int instances = 0;
	
	public Token() {
		//System.out.println("Token: " + ++instances);
	}
	
	public Token(Token other) {
		assign(this, other);
		//System.out.println("Token: " + ++instances);
	}
	
	public void reset() {
		next = null;
		ptr = 0;
		value = TOK.TOKreserved;
		string = null;
		len = 0;
		postfix = 0;
		ident = null;
		lineNumber = 0;
		leadingComment = null;
	}
	
	public static void assign(Token to, Token from) {
		to.next = from.next;
		to.ptr = from.ptr;
		to.value = from.value;
		to.string = from.string;
		to.len = from.len;
		to.postfix = from.postfix;
		//to.numberValue = from.numberValue;
		to.ident = from.ident;
		to.lineNumber = from.lineNumber;
		to.leadingComment = from.leadingComment;
	}
	
	@Override
	public String toString() {
		if (string != null) return string;
		
		if (value == TOK.TOKidentifier) {
			return ident.string;
		} else {
			return value.value;
		}
	}

}
