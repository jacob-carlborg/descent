package descent.internal.compiler.parser;

import java.math.BigDecimal;
import java.math.BigInteger;

import descent.core.dom.DDocComment;


public class Token {
	
	public Token next;
	public int ptr; // The start position of the token
	public TOK value;
	public String string; // the string value of the token, if any
	public int len; // The length of the token
	public int postfix;
	public BigInteger intValue;
	public BigDecimal floatValue; 
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
		intValue = null;
	}
	
	public static void assign(Token to, Token from) {
		to.next = from.next;
		to.ptr = from.ptr;
		to.value = from.value;
		to.string = from.string;
		to.len = from.len;
		to.postfix = from.postfix;
		to.intValue = from.intValue;
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
