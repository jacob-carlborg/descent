package descent.internal.compiler.parser;

import java.math.BigDecimal;

import descent.core.compiler.CharOperation;

// DMD 1.020
public class Token {
	
	public final static int SPECIAL__FILE__ = 1;
	public final static int SPECIAL__LINE__ = 2;
	public final static int SPECIAL__DATE__ = 3;
	public final static int SPECIAL__TIME__ = 4;
	public final static int SPECIAL__TIMESTAMP__ = 5;
	public final static int SPECIAL__VENDOR__ = 6;
	public final static int SPECIAL__VERSION__ = 7;
	
	public Token next;
	public int ptr; // The start position of the token
	public TOK value;
	public char[] string; // the string value of the token, if any. This is for string tokens.
	public char[] sourceString; // the raw source of the token. This is for any token.
	public int len; // The length of the token
	public int postfix;
	public integer_t intValue;
	public BigDecimal floatValue;
	public int lineNumber;
	public Comment leadingComment;
	public int special;
	
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
		sourceString = null;
		len = 0;
		postfix = 0;
		lineNumber = 0;
		leadingComment = null;
		intValue = null;
	}
	
	public static void assign(Token to, Token from) {
		to.next = from.next;
		to.ptr = from.ptr;
		to.value = from.value;
		to.sourceString = from.sourceString;
		to.len = from.len;
		to.postfix = from.postfix;
		to.intValue = from.intValue;
		to.lineNumber = from.lineNumber;
		to.leadingComment = from.leadingComment;
	}
	
	public void setString(char[] input, int start, int length) {
		this.sourceString = new char[length];
		System.arraycopy(input, start, this.sourceString, 0, length);
	}
	
	@Override
	public String toString() {
		return getRawTokenSourceAsString();
	}
	
	public char[] getRawTokenSource() {
		if (value == null) {
			return CharOperation.NO_CHAR;
		}
		switch(value) {
			case TOKeof:
				return CharOperation.NO_CHAR;
			case TOKint32v:
			case TOKuns32v:
			case TOKint64v:
			case TOKuns64v:
			case TOKfloat32v:
			case TOKfloat64v:
			case TOKfloat80v:
			case TOKimaginary32v:
			case TOKimaginary64v:
			case TOKimaginary80v:
			case TOKcharv:
			case TOKwcharv:
			case TOKdcharv:
			case TOKstring:
			case TOKlinecomment:
			case TOKdoclinecomment:
			case TOKblockcomment:
			case TOKdocblockcomment:
			case TOKpluscomment:
			case TOKdocpluscomment:
			case TOKwhitespace:
			case TOKPRAGMA:
			case TOKidentifier:
				return sourceString;
			default:
				return value.charArrayValue;
		}		
	}
	
	public String getRawTokenSourceAsString() {
		if (value == null) {
			return "";
		}
		switch(value) {
		case TOKeof:
			return "";
		case TOKint32v:
		case TOKuns32v:
		case TOKint64v:
		case TOKuns64v:
		case TOKfloat32v:
		case TOKfloat64v:
		case TOKfloat80v:
		case TOKimaginary32v:
		case TOKimaginary64v:
		case TOKimaginary80v:
		case TOKcharv:
		case TOKwcharv:
		case TOKdcharv:
		case TOKstring:
		case TOKlinecomment:
		case TOKdoclinecomment:
		case TOKblockcomment:
		case TOKdocblockcomment:
		case TOKpluscomment:
		case TOKdocpluscomment:
		case TOKwhitespace:
		case TOKPRAGMA:
			return new String(sourceString);
		case TOKidentifier:
			return new String(sourceString);
		default:
			return value.value;
	}	
	}

}

