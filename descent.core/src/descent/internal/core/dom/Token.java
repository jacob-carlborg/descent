package descent.internal.core.dom;


public class Token {
	
	public Token next;
	public int ptr; // The start position of the token
	public TOK value;
	public int blockCommentPtr; // The start position of the block comment, if any
	public int blockCommentLength; // The length of the block comment, if null
	public String blockComment;
	public int lineCommentPtr; // The start position of the line comment, if any
	public int lineCommentLength; // The length of the line comment, if null
	public String lineComment;
	
	public String ustring; // the string value of the token, if any
	public int len; // The length of the token
	public int postfix;
	
	public long numberValue; // Numeric value of the token, see if it is needed
	
	public Identifier ident;
	
	public Token() {
		
	}
	
	public Token(Token other) {
		this.next = other.next;
		this.ptr = other.ptr;
		this.value = other.value;
		this.blockCommentPtr = other.blockCommentPtr;
		this.blockCommentLength = other.blockCommentLength;
		this.blockComment = other.blockComment;
		this.lineCommentPtr = other.lineCommentPtr;
		this.lineCommentLength = other.lineCommentLength;
		this.lineComment = other.lineComment;
		this.ustring = other.ustring;
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
