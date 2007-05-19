package descent.internal.compiler.parser;

import static descent.internal.compiler.parser.TOK.*;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import descent.core.IProblemRequestor;
import descent.core.compiler.IProblem;
import descent.core.dom.AST;

/**
 * Internal lexer class.
 */
public class Lexer implements IProblemRequestor {
	
	private final static boolean IN_COMMENT = true;
	private final static boolean NOT_IN_COMMENT = false;
	
	private final static BigInteger X_FFFFFFFFFFFFFFFF =     new BigInteger("FFFFFFFFFFFFFFFF", 16);
	private final static BigInteger X_8000000000000000 =         new BigInteger("8000000000000000", 16);
	private final static BigInteger X_FFFFFFFF00000000  =       new BigInteger("FFFFFFFF00000000", 16);
	private final static BigInteger X_80000000 =                 new BigInteger("80000000", 16);
	private final static BigInteger X_FFFFFFFF80000000 =     new BigInteger("FFFFFFFF80000000", 16);
	                                                                       
	private final static int LS = 0x2028;
	private final static int PS = 0x2029;
	
	private StringTable stringtable = new StringTable();
	private OutBuffer stringbuffer = new OutBuffer();
	private Token freelist;
	
	public int linnum;
	
	public int base;
	public int p;
	public int end;
	public char[] input;
	public Loc loc;
	
	public Token token;
	public Token prevToken = new Token();
	
	public List<IProblem> problems;
	
	// TODO optimize and use an array of int
	public List<Integer> lineEnds;
	
	private boolean tokenizeComments;
	public boolean tokenizeWhiteSpace;
	private boolean tokenizePragmas;
	private boolean recordLineSeparator;
	protected final int apiLevel;
	
	/* package */ Lexer(int apiLevel) {
		this.apiLevel = apiLevel;
		initId();
		initKeywords();		
	}
	
	public void reset(char[] source, int offset, int length, boolean tokenizeComments, boolean tokenizePragmas, boolean tokenizeWhiteSpace, boolean recordLineSeparator) {
		this.problems = new ArrayList<IProblem>();
		
		// Make input larger and add zeros, to avoid comparing
		input = new char[length - base + 5];
		
		System.arraycopy(source, 0, input, 0, source.length);
		reset(offset, length);
	    this.tokenizeComments = tokenizeComments;
	    this.tokenizePragmas = tokenizePragmas;
		this.tokenizeWhiteSpace = tokenizeWhiteSpace;
		this.recordLineSeparator = recordLineSeparator;
		this.lineEnds = new ArrayList<Integer>();
		if (token == null) {
			token = new Token();
		} else {
			this.token.reset();
		}
	}
	
	public void reset(int offset, int length) {
		this.linnum = 1;
		this.base = offset;
		this.end = offset + length;
		this.p = offset;
	}
    
    public Lexer(String source, boolean tokenizeComments, boolean tokenizePragmas, boolean tokenizeWhiteSpace, boolean recordLineSeparator, int apiLevel) {
    	this(source.toCharArray(), tokenizeComments, tokenizePragmas, tokenizeWhiteSpace, recordLineSeparator, apiLevel);
    }
    
    public Lexer(char[] source, boolean tokenizeComments, boolean tokenizePragmas, boolean tokenizeWhiteSpace, boolean recordLineSeparator, int apiLevel) {
    	this(source, 0, source.length, tokenizeComments, tokenizePragmas, tokenizeWhiteSpace, recordLineSeparator, apiLevel);
    }
    
    public Lexer(String source, int offset, int length, boolean tokenizeComments, boolean tokenizePragmas, boolean tokenizeWhiteSpace, boolean recordLineSeparator, int apiLevel) {
    	this(source.toCharArray(), offset, length, tokenizeComments, tokenizePragmas, tokenizeWhiteSpace, recordLineSeparator, apiLevel);
    }
	
	public Lexer(char[] source, int offset, int length, boolean tokenizeComments, boolean tokenizePragmas, boolean tokenizeWhiteSpace, boolean recordLineSeparator, int apiLevel) {
		this(apiLevel);
		reset(source, offset, length, tokenizeComments, tokenizePragmas, tokenizeWhiteSpace, recordLineSeparator);
	}
	
	public void error(String message, int id, int line, int offset, int length) {
		problems.add(Problem.newSyntaxError(message, id, line, offset, length));
	}
	
	public void error(String message, int id, int line, ASTNode node) {
		problems.add(Problem.newSyntaxError(message, id, line, node.start, node.length));
	}
	
	public void error(String message, int id, int line, descent.core.dom.ASTNode node) {
		problems.add(Problem.newSyntaxError(message, id, line, node.getStartPosition(), node.getLength()));
	}
	
	public void error(String message, int id, Token token) {
		problems.add(Problem.newSyntaxError(message, id, token.lineNumber, token.ptr, token.len));
	}
	
	public void error(String message, int id, Token firstToken, Token secondToken) {
		problems.add(Problem.newSyntaxError(message, id, firstToken.lineNumber, firstToken.ptr, secondToken.ptr + secondToken.len - firstToken.ptr));
	}
	
	private Token newFreeListToken() {
		Token t;
		if (freelist != null) {
    		t = freelist;
    		freelist = t.next;
    	} else {
    		t = new Token();
    	}
		return t;
	}
	
	
	public TOK nextToken() {
		Token t;
		
		if (token != null && !(token.value == TOK.TOKlinecomment || token.value == TOK.TOKdoclinecomment ||
				token.value == TOK.TOKblockcomment || token.value == TOK.TOKdocblockcomment ||
				token.value == TOK.TOKpluscomment || token.value == TOK.TOKdocpluscomment)) {
			Token.assign(prevToken, token);
		}

	    if (token.next != null) {
	    	t = token.next;
	    	Token.assign(token, t);
	    	t.next = freelist;
	    	freelist = t;
	    } else {
	    	scan(token);
	    }
	    return token.value;
	}
	
	public Token peek(Token ct) {
		Token t;

	    if (ct.next != null) {
	    	t = ct.next;
	    } else {
	    	t = newFreeListToken();
	    	scan(t);
	    	t.next = null;
	    	ct.next = t;
	    }
	    return t;
	}
	
	public Token peekPastParen(Token tk)
	{
	    int parens = 1;
	    while (true) {
	    	tk = peek(tk);
	    	switch (tk.value)
	    	{
			    case TOKlparen:
					parens++;
					continue;
	
			    case TOKrparen:
					--parens;
					if (parens != 0)
					    continue;
					tk = peek(tk);
					break;
	
			    case TOKeof:
			    	break;
	
			    default:
			    	continue;
	    	}
	    	return tk;
	    }
	}
	
	public void scan(Token t) {
	    int linnum = this.linnum;
	    
	    t.lineNumber = linnum;
	    t.leadingComment = null;
	    
	    while (true)
	    {
		t.ptr = p;
		
		if (p > end) {
	    	t.value = TOK.TOKeof;
	    	return;
	    }
		
		switch (input[p])
		{
		    case 0:
		    case 0x1A:
			t.value = TOKeof;			// end of file
			t.len = 0;
			//lineEnds.add(p - 1);
			return;

		    case ' ':
		    case '\t':
		    // case '\v':
		    case '\f':
		    	p++;
		    	if (tokenizeWhiteSpace) {
		    		whitespace(t);
		    		return;
		    	}
			continue;			// skip white space

		    case '\r':
		    	p++;
				if (input[p] != '\n') {			// if CR stands by itself
					newline(NOT_IN_COMMENT);
				}
			    if (tokenizeWhiteSpace) {
			    	whitespace(t);
			    	return;
			    }
			continue;			// skip white space

		    case '\n':
		    	newline(NOT_IN_COMMENT);
		    	p++;
		    	if (tokenizeWhiteSpace) {
		    		whitespace(t);
		    		return;
		    	}
			continue;			// skip white space

		    case '0':  	case '1':   case '2':   case '3':   case '4':
		    case '5':  	case '6':   case '7':   case '8':   case '9':
			t.value = number(t);
			t.len = p - t.ptr;
			return;

	/*
	#ifdef CSTRINGS
		    case '\'':
			t->value = charConstant(t, 0);
			return;

		    case '"':
			t->value = stringConstant(t,0);
			return;

		    case 'l':
		    case 'L':
			if (p[1] == '\'')
			{
			    p++;
			    t->value = charConstant(t, 1);
			    return;
			}
			else if (p[1] == '"')
			{
			    p++;
			    t->value = stringConstant(t, 1);
			    return;
			}
	#else
	*/
		    case '\'':
			t.value = charConstant(t,0);
			return;

		    case 'r':
			if (input[p + 1] != '"') {
				case_ident(t);
		    	return;
			}
			p++;
		    case '`':
		    boolean hasR = input[p - 1] == 'r';
			t.value = wysiwygStringConstant(t, input[p]);
			if (hasR) {
				t.string = "r" + t.string;
			}
			t.len = p - t.ptr;
			return;

		    case 'x':
			if (input[p + 1] != '"') {
				case_ident(t);
		    	return;
			}
			p++;
			t.value = hexStringConstant(t);
			t.string = "x" + t.string;
			t.len = p - t.ptr;
			return;


		    case '"':
			t.value = escapeStringConstant(t,0);
			t.len = p - t.ptr;
			return;

		    case '\\': // escaped string literal
			{
				stringbuffer.reset();
				stringbuffer.data.append(input[p]);
				do {
					p++;
					escapeSequence(stringbuffer.data);
				} while (input[p] == '\\');
				t.len = p - t.ptr;
				t.string = stringbuffer.data.toString();
				t.postfix = 0;
				t.value = TOKstring;
				return;
			}

		    case 'l':
		    case 'L':
	//#endif
		    case 'a':  	case 'b':   case 'c':   case 'd':   case 'e':
		    case 'f':  	case 'g':   case 'h':   case 'i':   case 'j':
		    case 'k':  	            case 'm':   case 'n':   case 'o':
		    case 'p':  	case 'q': /*case 'r':*/ case 's':   case 't':
		    case 'u':  	case 'v':   case 'w': /*case 'x':*/ case 'y':
		    case 'z':
		    case 'A':  	case 'B':   case 'C':   case 'D':   case 'E':
		    case 'F':  	case 'G':   case 'H':   case 'I':   case 'J':
		    case 'K':  	            case 'M':   case 'N':   case 'O':
		    case 'P':  	case 'Q':   case 'R':   case 'S':   case 'T':
		    case 'U':  	case 'V':   case 'W':   case 'X':   case 'Y':
		    case 'Z':
		    case '_':
		    	case_ident(t);
		    	return;

		    case '/':
			p++;
			switch (input[p])
			{
			    case '=':
				p++;
				t.value = TOKdivass;
				t.len = 2;
				return;

			    case '*':
				p++;
				linnum = this.linnum;
				while (true)
				{
				    while (true)
				    {	char c = input[p];
					switch (c)
					{
					    case '/':
					    	break;

					    case '\n':
					    	newline(IN_COMMENT);
					    	p++;
					    	continue;

					    case '\r':
					    	p++;
					    	if (input[p] != '\n')
					    		newline(IN_COMMENT);
					    	continue;

					    case 0:
					    case 0x1A:
					    	error("Unterminated block comment", IProblem.UnterminatedBlockComment, t.lineNumber, t.ptr, p - t.ptr);
					    	t.value = TOKeof;
					    	p++;
					    	return;

					    default:
							if (c >= 0x80) {   
								int u = decodeUTF();
							    if (u == PS || u == LS) {
							    	newline(IN_COMMENT);
							    }
							}
							p++;
							continue;
					}
					break;
				    }
				    p++;
				    if (input[p - 2] == '*' && p - 3 != t.ptr)
					break;
				}
				
				if (tokenizeComments) {
					t.value = (input[t.ptr + 2] == '*' && p - 4 != t.ptr) ? TOKdocblockcomment : TOKblockcomment;
					t.len = p - t.ptr;
					t.string = new String(input, t.ptr, t.len);
					return;
				} else {
					continue;
				}

			    case '/':		// do // style comments
				linnum = this.linnum;
				while (true)
				{   char c = input[++p];
				    switch (c)
				    {
					case '\n':
					    break;

					case '\r':
					    if (input[p + 1] == '\n') {
					    	p++;
					    }
					    break;

					case 0:
					case 0x1A:
						if (tokenizeComments) {
							t.value = input[t.ptr + 2] == '/' ? TOKdoclinecomment : TOKlinecomment;
							t.len = p - t.ptr;
							t.string = new String(input, t.ptr, t.len);
							return;
						}
						t.value = TOKeof;
						return;

					default:
					    if (c >= 0x80)
					    {   int u = decodeUTF();
						if (u == PS || u == LS)
						    break;
					    }
					    continue;
				    }
				    break;
				}
				
				if (tokenizeComments) {
					t.value = input[t.ptr + 2] == '/' ? TOKdoclinecomment : TOKlinecomment;
					t.len = p - t.ptr + 1;
					t.string = new String(input, t.ptr, t.len);
					
					newline(NOT_IN_COMMENT);
					p++;					
					return;
				} else {
					newline(NOT_IN_COMMENT);
					p++;
					continue;
				}

			    case '+':
			    {	int nest;

				linnum = this.linnum;
				p++;
				nest = 1;
				while (true)
				{   char c = input[p];
				    switch (c)
				    {
					case '/':
					    p++;
					    if (input[p] == '+')
					    {
						p++;
						nest++;
					    }
					    continue;

					case '+':
					    p++;
					    if (input[p] == '/')
					    {
						p++;
						if (--nest == 0)
						    break;
					    }
					    continue;

					case '\r':
					    p++;
					    if (input[p] != '\n') {
					    	newline(IN_COMMENT);
					    }
					    continue;

					case '\n':
						newline(IN_COMMENT);
					    p++;
					    continue;

					case 0:
					case 0x1A:
						error("Unterminated plus block comment", IProblem.UnterminatedPlusBlockComment, t.lineNumber, t.ptr, p - t.ptr);
					    t.value = TOKeof;
					    p++;
					    return;

					default:
					    if (c >= 0x80)
					    {   int u = decodeUTF();
						if (u == PS || u == LS)
						    newline(IN_COMMENT);
					    }
					    p++;
					    continue;
				    }
				    break;
				}
				
				if (tokenizeComments) {
					t.value = (input[t.ptr + 2] == '+' && p - 4 != t.ptr) ? TOKdocpluscomment : TOKpluscomment;
					t.len = p - t.ptr;
					t.string = new String(input, t.ptr, t.len);
					return;
				} else {
					continue;
				}
			    }
			}
			t.value = TOKdiv;
			t.len = 1;
			return;

		    case '.':
			p++;
			if (Chars.isdigit(input[p]))
			{
			    p--;
			    t.value = inreal(t);
			    t.len = p - t.ptr;
			}
			else if (input[p] == '.')
			{
			    if (input[p + 1] == '.')
			    {   p += 2;
				t.value = TOKdotdotdot;
				t.len = 3;
			    }
			    else
			    {   p++;
				t.value = TOKslice;
				t.len = 2;
			    }
			}
			else {
			    t.value = TOKdot;
				t.len = 1;
			}
			return;

		    case '&':
			p++;
			if (input[p] == '=')
			{   p++;
			    t.value = TOKandass;
			    t.len = 2;
			}
			else if (input[p] == '&')
			{   p++;
			    t.value = TOKandand;
			    t.len = 2;
			}
			else {
			    t.value = TOKand;
			    t.len = 1;
			}
			return;

		    case '|':
			p++;
			if (input[p] == '=')
			{   p++;
			    t.value = TOKorass;
			    t.len = 2;
			}
			else if (input[p] == '|')
			{   p++;
			    t.value = TOKoror;
			    t.len = 2;
			}
			else {
			    t.value = TOKor;
				t.len = 1;
			}
			return;

		    case '-':
			p++;
			if (input[p] == '=')
			{   p++;
			    t.value = TOKminass;
			    t.len = 2;
			}
	/*
	#if 0
			else if (input[p] == '>')
			{   p++;
			    t.value = TOKarrow;
			}
	#endif
	*/
			else if (input[p] == '-')
			{   p++;
			    t.value = TOKminusminus;
			    t.len = 2;
			}
			else {
			    t.value = TOKmin;
			    t.len = 1;
			}
			return;

		    case '+':
			p++;
			if (input[p] == '=')
			{   p++;
			    t.value = TOKaddass;
			    t.len = 2;
			}
			else if (input[p] == '+')
			{   p++;
			    t.value = TOKplusplus;
			    t.len = 2;
			}
			else {
			    t.value = TOKadd;
			    t.len = 1;
			}
			return;

		    case '<':
			p++;
			if (input[p] == '=')
			{   p++;
			    t.value = TOKle;			// <=
			    t.len = 2;
			}
			else if (input[p] == '<')
			{   p++;
			    if (input[p] == '=')
			    {   p++;
				t.value = TOKshlass;		// <<=
				t.len = 3;
			    }
			    else {
				t.value = TOKshl;		// <<
				t.len = 2;
			    }
			}
			else if (input[p] == '>')
			{   p++;
			    if (input[p] == '=')
			    {   p++;
				t.value = TOKleg;		// <>=
				t.len = 3;
			    }
			    else {
				t.value = TOKlg;		// <>
				t.len = 2;
			    }
			}
			else {
			    t.value = TOKlt;			// <
			    t.len = 1;
			}
			return;

		    case '>':
			p++;
			if (input[p] == '=')
			{   p++;
			    t.value = TOKge;			// >=
			    t.len = 2;
			}
			else if (input[p] == '>')
			{   p++;
			    if (input[p] == '=')
			    {   p++;
				t.value = TOKshrass;		// >>=
				t.len = 3;
			    }
			    else if (input[p] == '>')
			    {	p++;
				if (input[p] == '=')
				{   p++;
				    t.value = TOKushrass;	// >>>=
				    t.len = 4;
				}
				else {
				    t.value = TOKushr;		// >>>
				    t.len = 3;
				}
			    }
			    else {
				t.value = TOKshr;		// >>
				t.len = 2;
			    }
			}
			else {
			    t.value = TOKgt;			// >
			    t.len = 1;
			}
			return;

		    case '!':
			p++;
			if (input[p] == '=')
			{   p++;
			    if (input[p] == '=' && apiLevel == AST.D1)
			    {	p++;
				t.value = TOKnotidentity;	// !==
				t.len = 3;
			    }
			    else {
				t.value = TOKnotequal;		// !=
				t.len = 2;
			    }
			}
			else if (input[p] == '<')
			{   p++;
			    if (input[p] == '>')
			    {	p++;
				if (input[p] == '=')
				{   p++;
				    t.value = TOKunord; // !<>=
				    t.len = 4;
				}
				else {
				    t.value = TOKue;	// !<>
				    t.len = 3;
				}
			    }
			    else if (input[p] == '=')
			    {	p++;
				t.value = TOKug;	// !<=
				t.len = 3;
			    }
			    else {
				t.value = TOKuge;	// !<
				t.len = 2;
			    }
			}
			else if (input[p] == '>')
			{   p++;
			    if (input[p] == '=')
			    {	p++;
				t.value = TOKul;	// !>=
				t.len = 3;
			    }
			    else {
				t.value = TOKule;	// !>
				t.len = 2;
			    }
			}
			else {
			    t.value = TOKnot;		// !
			    t.len = 1;
			}
			return;

		    case '=':
			p++;
			if (input[p] == '=')
			{   p++;
			    if (input[p] == '=' && apiLevel == AST.D1)
			    {	p++;
				t.value = TOKidentity;		// ===
				t.len = 3;
			    }
			    else {
				t.value = TOKequal;		// ==
			    t.len = 2;
			    }
			}
			else {
			    t.value = TOKassign;		// =
			    t.len = 1;
			}
			return;

		    case '~':
			p++;
			if (input[p] == '=')
			{   p++;
			    t.value = TOKcatass;		// ~=
			    t.len = 2;
			}
			else {
			    t.value = TOKtilde;		// ~
			    t.len = 1;
			}
			return;
		    case '(': p++; t.value = TOKlparen; t.len = 1; return;
		    case ')': p++; t.value = TOKrparen; t.len = 1; return;
		    case '[': p++; t.value = TOKlbracket; t.len = 1; return;
		    case ']': p++; t.value = TOKrbracket; t.len = 1; return;
		    case '{': p++; t.value = TOKlcurly; t.len = 1; return;
		    case '}': p++; t.value = TOKrcurly; t.len = 1; return;
		    case '?': p++; t.value = TOKquestion; t.len = 1; return;
		    case ',': p++; t.value = TOKcomma; t.len = 1; return;
		    case ';': p++; t.value = TOKsemicolon; t.len = 1; return;
		    case ':': p++; t.value = TOKcolon; t.len = 1; return;
		    case '$': p++; t.value = TOK.TOKdollar; t.len = 1; return;
		    
		    case '*':
			p++;
			if (input[p] == '=')
			{   p++;
			    t.value = TOKmulass;
			    t.len = 2;
			}
			else {
			    t.value = TOKmul;
			    t.len = 1;
			}
			return;
			
		    case '%':
				p++;
				if (input[p] == '=')
				{   p++;
				    t.value = TOKmodass;
				    t.len = 2;
				}
				else {
				    t.value = TOKmod;
				    t.len = 1;
				}
				return;
				
		    case '^':
				p++;
				if (input[p] == '=')
				{   p++;
				    t.value = TOKxorass;
				    t.len = 2;
				}
				else {
				    t.value = TOKxor;
				    t.len = 1;
				}
				return;

		    case '#':
			p++;
			pragma(t);
			if (tokenizePragmas) {
				return;
			}
			continue;

		    default: {
				char c = input[p];

				if (c >= 0x80) {
					int u = decodeUTF();

					// Check for start of unicode identifier
					if (UniAlpha.isUniAlpha(u)) {
						case_ident(t);
						return;
					}

					if (u == PS || u == LS) {
						newline(NOT_IN_COMMENT);
						p++;
						if (tokenizeWhiteSpace) {
							whitespace(t);
							return;
						}
						continue;
					}
				}
				if (Chars.isprint(c)) {
					error("Unsupported char: " + (char) c,
							IProblem.UnsupportedCharacter, t.lineNumber, p, 1);
				} else {
					error("Unsupported char: 0x" + Integer.toHexString(c),
							IProblem.UnsupportedCharacter, t.lineNumber, p, 1);
				}
				p++;
				continue;
			}
			}
	    }
	}

	private void case_ident(Token t) {
		char c;
		StringValue sv;
		Identifier id;

		do
		{
			c = input[++p];
		} while (c > 0 && Chars.isidchar(c) || (c >= 0x80 && UniAlpha.isUniAlpha(decodeUTF())));
		sv = stringtable.update(input, t.ptr, p - t.ptr);
		id = (Identifier) sv.ptrvalue;
		if (id == null)
		{   id = new Identifier(sv.lstring, TOKidentifier);
		    sv.ptrvalue = id;
		}
		t.ident = id;
		t.value = id.value;
		t.len = id.string.length();
		return;
	}
	
	private int escapeSequence(StringBuilder stringBuilder) {
		int c;
		int n;
		int ndigits = 0;
		int startOfNumber;

		c = input[p];
		stringBuilder.append((char) c);
		switch (c) {
		case '\'':
		case '"':
		case '?':
		case '\\':
			p++;
			break;

		case 'a':
			c = 7;
			p++;
			break;
		case 'b':
			c = 8;
			p++;
			break;
		case 'f':
			c = 12;
			p++;
			break;
		case 'n':
			c = 10;
			p++;
			break;
		case 'r':
			c = 13;
			p++;
			break;
		case 't':
			c = 9;
			p++;
			break;
		case 'v':
			c = 11;
			p++;
			break;

		case 'u':
			ndigits = 4;
		case 'U':
			if (ndigits == 0)
				ndigits = 8;
		case 'x':
			startOfNumber = p - 1;
			if (ndigits == 0) {
				ndigits = 2;
			}
			p++;
			c = input[p];
			stringBuilder.append((char) c);
			if (Chars.ishex(c)) {
				long v;

				n = 0;
				v = 0;
				while (true) {
					if (Chars.isdigit(c)) {
						c -= '0';
					} else if (Chars.islower(c)) {
						c -= 'a' - 10;
					} else {
						c -= 'A' - 10;
					}
					v = v * 16 + c;
					c = input[++p];
					if (++n == ndigits) {
						break;
					}
					stringBuilder.append((char) c);
					if (!Chars.ishex(c)) {
						error(
								"Escape hex sequence has " + n
										+ " digits instead of " + ndigits,
								IProblem.IncorrectNumberOfHexDigitsInEscapeSequence,
								linnum, startOfNumber, p - startOfNumber);
						break;
					}
				}
				if (ndigits != 2 && !Utf.isValidDchar(v)) {
					error("Invalid UTF character: \\U" + Long.toHexString(v),
							IProblem.InvalidUtfCharacter, linnum, startOfNumber, p
									- startOfNumber);
				}
				c = (int) v;
			} else {
				error("Undefined escape hex sequence",
						IProblem.UndefinedEscapeHexSequence, linnum, startOfNumber,
						p - startOfNumber + 1);
			}
			break;

		case '&': // named character entity
			for (int idstart = ++p; true; p++) {
				switch (input[p]) {
				case ';':
					c = Entity.HtmlNamedEntity(input, idstart, p - idstart, linnum,
							this);
					if (c == ~0) {
						// error("unnamed character entity &%.*s;", p - idstart,
						// idstart);
						c = ' ';
					}
					p++;
					break;

				default:
					if (Chars.isalpha(input[p])
							|| (p != idstart + 1 && Chars.isdigit(input[p])))
						continue;
					error("Unterminated named entity",
							IProblem.UnterminatedNamedEntity, linnum, idstart - 1, p
									- idstart + 1);
					break;
				}
				break;
			}
			break;

		case 0:
		case 0x1A: // end of file
			c = '\\';
			break;

		default:
			if (Chars.isoctal(c)) {
				int v;

				n = 0;
				v = 0;
				do {
					v = v * 8 + (c - '0');
					c = input[++p];
					if (n + 1 < 3 && Chars.isoctal(c)) {
						stringBuilder.append((char) c);
					}
				} while (++n < 3 && Chars.isoctal(c));
				c = v;
			} else {
				error("Undefined escape sequence", 
						IProblem.UndefinedEscapeSequence, linnum, p - 1, 2);
			}
			break;
		}
		return c;
	}
	
	private TOK wysiwygStringConstant(Token t, int tc) {
		int c;
		
		stringbuffer.reset();
		stringbuffer.data.append(input[p]);

	    p++;
	    while (true)
	    {
		c = input[p++];
		stringbuffer.data.append((char) c);
		switch (c)
		{
		    case '\n':
		    	newline(NOT_IN_COMMENT);
		    	break;

		    case '\r':
			if (input[p] == '\n') {
				stringbuffer.data.append(input[p]);
			    continue;	// ignore
			}
			c = '\n';	// treat EndOfLine as \n character
			newline(NOT_IN_COMMENT);
			break;

		    case 0:
		    case 0x1A: {
		    	error("Unterminated string constant", IProblem.UnterminatedStringConstant, token.lineNumber, token.ptr, p - token.ptr - 1);
				t.string = "";
				t.len = 0;
				t.postfix = 0;
				return TOKstring;
		    }

		    case '"':
		    case '`':
			if (c == tc)
			{
			    stringPostfix(t, stringbuffer.data);
			    t.len = stringbuffer.data.length();
			    t.string = stringbuffer.data.toString();
			    return TOKstring;
			}
			break;

		    default:
			if (c >= 0x80)
			{   p--;
			    int u = decodeUTF();
			    p++;
			    if (u == PS || u == LS)
				newline(NOT_IN_COMMENT);
			    stringbuffer.writeUTF8(u);
			    continue;
			}
			break;
		}
	    }
	}
	
	private TOK hexStringConstant(Token t) {
		int c;
	    int n = 0;
	    int v = 0;

	    p++;
	    stringbuffer.reset();
	    stringbuffer.data.append("\"");
	    while (true)
	    {
		c = input[p++];
		stringbuffer.data.append((char) c);
		switch (c)
		{
		    case ' ':
		    case '\t':
		    // case '\v':
		    case '\f':
		    	continue;			// skip white space

		    case '\r':
			if (input[p] == '\n') {
				stringbuffer.data.append(input[p]);
			    continue;			// ignore
			}
			// Treat isolated '\r' as if it were a '\n'
		    case '\n':
		    	newline(NOT_IN_COMMENT);
			continue;

		    case 0:
		    case 0x1A: {
		    	error("Unterminated string constant", IProblem.UnterminatedStringConstant, token.lineNumber, token.ptr, p - token.ptr - 1);
				t.string = "";
				t.len = 0;
				t.postfix = 0;
				return TOKstring;
		    }

		    case '"':
			if ((n & 1) != 0)
			{   
				error("Odd number (" + n + ") of hex characters in hex string", IProblem.OddNumberOfCharactersInHexString, token.lineNumber, token.ptr, p - token.ptr);
			    stringbuffer.writeByte(v);
			}
			stringPostfix(t, stringbuffer.data);
			t.len = stringbuffer.data.length();
			t.string = stringbuffer.data.toString();
			return TOKstring;

		    default:
			if (c >= '0' && c <= '9')
			    c -= '0';
			else if (c >= 'a' && c <= 'f')
			    c -= 'a' - 10;
			else if (c >= 'A' && c <= 'F')
			    c -= 'A' - 10;
			else if (c >= 0x80)
			{   p--;
			    int u = decodeUTF();
			    p++;
			    if (u == PS || u == LS)
				newline(NOT_IN_COMMENT);
			    else {
			    	error("Non-hex character: " + (char) u, IProblem.NonHexCharacter, linnum, p - 1, 1);
			    }
			}
			else {
			    error("Non-hex character: " + (char) c, IProblem.NonHexCharacter, linnum, p - 1, 1);
			}
			if ((n & 1) != 0)
			{   v = (v << 4) | c;
			    //stringbuffer.writeByte(v);
			}
			else
			    v = c;
			n++;
			break;
		}
	    }
	}
	
	private TOK escapeStringConstant(Token t, int wide) {
		int c;
	    //Loc start = loc;

		stringbuffer.reset();
		stringbuffer.data.append(input[p]);
		
	    p++;
	    
	    while (true)
	    {
		c = input[p++];
		switch (c)
		{
		    case '\\':
			switch (input[p])
			{
			    case 'u':
			    case 'U':
			    case '&':
				c = escapeSequence(stringbuffer.data);
				continue;

			    default:
				c = escapeSequence(stringbuffer.data);
				break;
			}
			break;

		    case '\n':
		    	newline(NOT_IN_COMMENT);
			break;

		    case '\r':
			if (input[p] == '\n') {
			    continue;	// ignore
			}
			c = '\n';	// treat EndOfLine as \n character
			newline(NOT_IN_COMMENT);
			break;

		    case '"':
		    	stringbuffer.data.append("\"");
				stringPostfix(t, stringbuffer.data);
				t.len = stringbuffer.data.length();
				t.string = stringbuffer.data.toString();
			return TOKstring;

		    case 0:
		    case 0x1A: {
				p--;
				error("Unterminated string constant", IProblem.UnterminatedStringConstant, token.lineNumber, token.ptr, p - token.ptr);
				
				t.string = "";
				t.len = 0;
				t.postfix = 0;
				return TOKstring;
		    }

		    default:
			if (c >= 0x80)
			{
			    p--;
			    c = decodeUTF();
			    if (c == LS || c == PS)
			    {	c = '\n';
			    newline(NOT_IN_COMMENT);
			    }
			    p++;
			    stringbuffer.writeUTF8(c);
			    continue;
			}
			break;
		}
		stringbuffer.writeByte(c);
	    }
	}
	
	private TOK charConstant(Token t, int wide) {
		int c;
		TOK tk = TOKcharv;
		
		StringBuilder fullChar = new StringBuilder();
		fullChar.append("'");

		p++;
		c = input[p++];
		fullChar.append((char) c);		
		
		switch (c) {
		case '\\':
			switch (input[p]) {
			case 'u':
				escapeSequence(fullChar);
				t.intValue = BigInteger.valueOf(escapeSequence(fullChar));
				tk = TOKwcharv;
				p--;
				break;

			case 'U':
			case '&':
				t.intValue = BigInteger.valueOf(escapeSequence(fullChar));
				escapeSequence(fullChar);
				tk = TOKdcharv;
				p--;
				break;

			default:
				t.intValue = BigInteger.valueOf(escapeSequence(fullChar));
				escapeSequence(fullChar);
				p--;
				break;
			}
			break;

		case '\n':
			newline(NOT_IN_COMMENT);
		case '\r':
		case 0:
		case 0x1A:
		case '\'': {
			error("Unterminated character constant", 
					IProblem.UnterminatedCharacterConstant, token.lineNumber, token.ptr, p
							- token.ptr);
			t.intValue = BigInteger.ZERO;
			return tk;
		}

		default:
			if (c >= 0x80) {
				p--;
				c = decodeUTF();
				p++;
				if (c == LS || c == PS) {
					newline(NOT_IN_COMMENT);
					error("Unterminated character constant",
							IProblem.UnterminatedCharacterConstant,
							token.lineNumber, token.ptr, p - token.ptr);
					t.intValue = BigInteger.ZERO;
					return tk;
				}
				if (c < 0xD800 || (c >= 0xE000 && c < 0xFFFE))
					tk = TOKwcharv;
				else
					tk = TOKdcharv;
			}
			fullChar.append("'");
			break;
		}
		
		t.string = new String(fullChar);
		t.intValue = BigInteger.valueOf(c);
		t.len = p - t.ptr + 1;

		if (input[p] != '\'') {
			error("Unterminated character constant", 
					IProblem.UnterminatedCharacterConstant, token.lineNumber, token.ptr, p
							- token.ptr);
			return tk;
		}
		p++;
		return tk;
	}
	
	private void stringPostfix(Token t, StringBuilder stringBuilder) {
		switch (input[p])
	    {
		case 'c':
		case 'w':
		case 'd':
			stringBuilder.append(input[p]);
		    t.postfix = input[p];
		    p++;
		    break;

		default:
		    t.postfix = 0;
		    break;
	    }
	}
	
	// #if 0
	/*
	private int wchar(int u) {
		int value;
	    int n;
	    int c;
	    int nchars;

	    nchars = (u == 'U') ? 8 : 4;
	    value = 0;
	    for (n = 0; true; n++)
	    {
		++p;
		if (n == nchars)
		    break;
		c = input[p];
		if (!Chars.ishex(c))
		{   error("\\%c sequence must be followed by %d hex characters", u, nchars);
		    break;
		}
		if (Chars.isdigit(c))
		    c -= '0';
		else if (Chars.islower(c))
		    c -= 'a' - 10;
		else
		    c -= 'A' - 10;
		value <<= 4;
		value |= c;
	    }
	    return value;
	}
	*/
	// #endif
	
	enum STATE { STATE_initial, STATE_0, STATE_decimal, STATE_octal, STATE_octale,
		STATE_hex, STATE_binary, STATE_hex0, STATE_binary0,
		STATE_hexh, STATE_error };
		
	private final static int FLAGS_decimal = 1;
	private final static int FLAGS_unsigned = 2;
	private final static int FLAGS_long = 4;
	
	private TOK number(Token t) {
		
		// We use a state machine to collect numbers
		STATE state;

		int flags = FLAGS_decimal;

		@SuppressWarnings("unused")
		int i;
		@SuppressWarnings("unused")
		int base;
		int c;
		int start;
		TOK result;
		
		StringBuilder fullNumber = new StringBuilder();

		state = STATE.STATE_initial;
		base = 0;
		stringbuffer.reset();
		start = p;

	goto_done: 
		while (true) {
			boolean writeToStringBuffer = true;
			c = input[p];
			switch (state) {
			case STATE_initial: // opening state
				if (c == '0')
					state = STATE.STATE_0;
				else
					state = STATE.STATE_decimal;
				break;

			case STATE_0:
				flags = (flags & ~FLAGS_decimal);
				switch (c) {
				// #if ZEROH
				/*
				 * case 'H': // 0h case 'h': goto hexh;
				 */
				// #endif
				case 'X':
				case 'x':
					state = STATE.STATE_hex0;
					break;

				case '.':
					if (input[p + 1] == '.') // .. is a separate token
						break goto_done;
				case 'i':
				case 'f':
				case 'F':
					p = start;
					return inreal(t);
					// #if ZEROH
					/*
					 * case 'E': case 'e': goto case_hex;
					 */
					// #endif
				case 'B':
				case 'b':
					state = STATE.STATE_binary0;
					break;

				case '0':
				case '1':
				case '2':
				case '3':
				case '4':
				case '5':
				case '6':
				case '7':
					state = STATE.STATE_octal;
					break;

				// #if ZEROH
				/*
				 * case '8': case '9': case 'A': case 'C': case 'D': case 'F':
				 * case 'a': case 'c': case 'd': case 'f': case_hex: state =
				 * STATE.STATE_hexh; break;
				 */
				// #endif
				case '_':
					state = STATE.STATE_octal;
					writeToStringBuffer = false;
					break;
					
			    case 'L':
					if (input[p + 1] == 'i') {
					    //goto real;
						p = start;
						return inreal(t);
					}
					break goto_done;

				default:
					break goto_done;
				}
				break;

			case STATE_decimal: // reading decimal number
				if (!Chars.isdigit(c)) {
					// #if ZEROH
					/*
					 * if (Chars.ishex(c) || c == 'H' || c == 'h' ) goto hexh;
					 */
					// #endif
					if (c == '_') // ignore embedded _
					{
						writeToStringBuffer = false;
						break;
					}
					if (c == '.' && input[p + 1] != '.') {
						p = start;
						return inreal(t);
					} else if (c == 'i' || c == 'f' || c == 'F' || c == 'e'
							|| c == 'E') {
						// real:
						// It's a real number. Back up and rescan as a real
						p = start;
						return inreal(t);
					} else if (c == 'L' && input[p + 1] == 'i') {
						// goto real;
						p = start;
						return inreal(t);
					}

					break goto_done;
				}
				break;

			case STATE_hex0: // reading hex number
			case STATE_hex:
				if (!Chars.ishex(c)) {
					if (c == '_') // ignore embedded _
					{
						writeToStringBuffer = false;
						break;
					}
					if (c == '.' && input[p + 1] != '.') {
						p = start;
						return inreal(t);
					}
					if (c == 'P' || c == 'p' || c == 'i') {
						p = start;
						return inreal(t);
					}
					if (state == STATE.STATE_hex0) {
						error("Hex digit expected, not " + (char) c,
								IProblem.HexDigitExpected, linnum, p, 1);
					}
					break goto_done;
				}
				state = STATE.STATE_hex;
				break;

			// #if ZEROH
			/*
			 * hexh: state = STATE.STATE_hexh; case STATE_hexh: // parse numbers
			 * like 0FFh if (!Chars.ishex(c)) { if (c == 'H' || c == 'h') { p++;
			 * base = 16; break goto_done; } else { // Check for something like
			 * 1E3 or 0E24 if (memchr((char *)stringbuffer.data, 'E',
			 * stringbuffer.offset) || memchr((char *)stringbuffer.data, 'e',
			 * stringbuffer.offset)) goto real; error("Hex digit expected, not
			 * '%c'", c); break goto_done; } } break;
			 */
			// #endif
			case STATE_octal: // reading octal number
			case STATE_octale: // reading octal number with non-octal digits
				if (!Chars.isoctal(c)) {
					// #if ZEROH
					/*
					 * if (Chars.ishex(c) || c == 'H' || c == 'h' ) goto hexh;
					 */
					// #endif
					if (c == '_') // ignore embedded _
					{
						writeToStringBuffer = false;
						break;
					}
					if (c == '.' && input[p + 1] != '.') {
						p = start;
						return inreal(t);
					}
					if (c == 'i') {
						p = start;
						return inreal(t);
					}
					if (Chars.isdigit(c)) {
						state = STATE.STATE_octale;
					} else
						break goto_done;
				}
				break;

			case STATE_binary0: // starting binary number
			case STATE_binary: // reading binary number
				if (c != '0' && c != '1') {
					// #if ZEROH
					/*
					 * if (Chars.ishex(c) || c == 'H' || c == 'h' ) goto hexh;
					 */
					// #endif
					if (c == '_') // ignore embedded _
					{
						writeToStringBuffer = false;
						break;
					}
					if (state == STATE.STATE_binary0) {
						error("Binary digit expected",
								IProblem.BinaryDigitExpected, linnum, p, 1);
						state = STATE.STATE_error;
						break;
					} else
						break goto_done;
				}
				state = STATE.STATE_binary;
				break;

			case STATE_error: // for error recovery
				if (!Chars.isdigit(c)) // scan until non-digit
					break goto_done;
				break;

			default:
				throw new IllegalStateException("Can't happen");
			}
			if (writeToStringBuffer) {
				stringbuffer.writeByte(c);
			}
			fullNumber.append((char) c);
			p++;
		}
		
		if (state == STATE.STATE_octale) {
			error("Octal digit expected", 
					IProblem.OctalDigitExpected, linnum, p - 1, 1);
		}

		// uinteger_t n; // unsigned >=64 bit integer type
		BigInteger n; // unsigned >=64 bit integer type
		boolean integerOverflow = false;

		if (stringbuffer.data.length() == 2
				&& (state == STATE.STATE_decimal || state == STATE.STATE_0))
			n = new BigInteger(String.valueOf(stringbuffer.data.charAt(0) - '0'));
		else {
			// Convert string to integer
			// Ary sais: changed to use BigInteger 
			int p = 0;
			int r = 10;
			
			if (stringbuffer.data.charAt(0) == '0' && stringbuffer.data.length() > 1) {
				if (stringbuffer.data.charAt(1) == 'x' || stringbuffer.data.charAt(1) == 'X') {
					p = 2;
					r = 16;
				} else if (stringbuffer.data.charAt(1) == 'b' || stringbuffer.data.charAt(1) == 'B') {
					p = 2;
					r = 2;
				} else if (Chars.isdigit(stringbuffer.data.charAt(1))) {
					p = 1;
					r = 8;
				}
			}
			
			try {
				n = new BigInteger(stringbuffer.data.substring(p), r);
			} catch (NumberFormatException ex) {
				n = BigInteger.ZERO;
			}
			if (n.compareTo(X_FFFFFFFFFFFFFFFF) > 0) {
				integerOverflow = true;
			}
		}

		// Parse trailing 'u', 'U', 'l' or 'L' in any combination
		while (true) {
			char f;

			switch (input[p]) {
			case 'U':
			case 'u':
				f = FLAGS_unsigned;
				p++;
				if ((flags & f) != 0) {
					error("Unrecognized token",
							IProblem.UnrecognizedToken,
							linnum, p - 1, 1);
				}
				flags = (flags | f);
				fullNumber.append(input[p - 1]);				
				continue;

			case 'l':
				// if (!global.params.useDeprecated)
				error("'l' suffix is deprecated, use 'L' instead",
						IProblem.LSuffixDeprecated,
						linnum, p, 1);
			case 'L':
				f = FLAGS_long;
				p++;
				if ((flags & f) != 0) {
					error("Unrecognized token",
							IProblem.UnrecognizedToken,
							linnum, p - 1, 1);
				}
				flags = (flags | f);
				fullNumber.append(input[p - 1]);
				continue;
			default:
				break;
			}
			break;
		}

		if (integerOverflow) {
			error("Integer overflow", 
					IProblem.IntegerOverflow, t.lineNumber, t.ptr, p - start);
		}

		switch (flags) {
		case 0:
			/*
			 * Octal or Hexadecimal constant. First that fits: int, uint, long,
			 * ulong
			 */
			if (n.and(X_8000000000000000).compareTo(BigInteger.ZERO) != 0)
				result = TOKuns64v;
			else if (n.and(X_FFFFFFFF00000000).compareTo(BigInteger.ZERO) != 0)
				result = TOKint64v;
			else if (n.and(X_80000000).compareTo(BigInteger.ZERO) != 0)
				result = TOKuns32v;
			else
				result = TOKint32v;
			break;

		case FLAGS_decimal:
			/*
			 * First that fits: int, long, long long
			 */
			if (n.and(X_8000000000000000).compareTo(BigInteger.ZERO) != 0) {
				error("Signed integer overflow",
						IProblem.SignedIntegerOverflow, linnum, start, p - start);
				result = TOKuns64v;
			} else if (n.and(X_FFFFFFFF80000000).compareTo(BigInteger.ZERO) != 0)
				result = TOKint64v;
			else
				result = TOKint32v;
			break;

		case FLAGS_unsigned:
		case FLAGS_decimal | FLAGS_unsigned:
			/*
			 * First that fits: uint, ulong
			 */
			if (n.and(X_FFFFFFFF00000000).compareTo(BigInteger.ZERO) != 0)
				result = TOKuns64v;
			else
				result = TOKuns32v;
			break;

		case FLAGS_decimal | FLAGS_long:
			if (n.and(X_8000000000000000).compareTo(BigInteger.ZERO) != 0) {
				error("Signed integer overflow",
						IProblem.SignedIntegerOverflow, linnum, start, p - start);
				result = TOKuns64v;
			} else
				result = TOKint64v;
			break;

		case FLAGS_long:
			if (n.and(X_8000000000000000).compareTo(BigInteger.ZERO) != 0)
				result = TOKuns64v;
			else
				result = TOKint64v;
			break;

		case FLAGS_unsigned | FLAGS_long:
		case FLAGS_decimal | FLAGS_unsigned | FLAGS_long:
			result = TOKuns64v;
			break;

		default:
			throw new IllegalStateException("Can't happen");
		}
		t.string = new String(fullNumber);
		t.intValue = n;
		return result;
	}
	
	private TOK inreal(Token t) {
		int dblstate;
		int c;
		int hex; // is this a hexadecimal-floating-constant?
		TOK result;
		
		StringBuilder fullNumber = new StringBuilder();

		stringbuffer.reset();
		dblstate = 0;
		hex = 0;
	goto_done: 
		while (true) {
			// Get next char from input
			c = input[p++];
			boolean writeByte = true;
		inner_while: 
			while (true) {
				switch (dblstate) {
				case 0: // opening state
					if (c == '0')
						dblstate = 9;
					else if (c == '.')
						dblstate = 3;
					else
						dblstate = 1;
					break;

				case 9:
					dblstate = 1;
					if (c == 'X' || c == 'x') {
						hex++;
						break;
					}
				case 1: // digits to left of .
				case 3: // digits to right of .
				case 7: // continuing exponent digits
					if (!Chars.isdigit(c) && !(hex != 0 && Chars.ishex(c))) {
						if (c == '_') {
							writeByte = false;
							break inner_while; // ignore embedded '_'
						}
						dblstate++;
						continue;
					}
					break;

				case 2: // no more digits to left of .
					if (c == '.') {
						dblstate++;
						break;
					}
				case 4: // no more digits to right of .
					if ((c == 'E' || c == 'e') || hex != 0
							&& (c == 'P' || c == 'p')) {
						dblstate = 5;
						hex = 0; // exponent is always decimal
						break;
					}
					if (hex != 0) {
						error("Binary-exponent-part required",
								IProblem.BinaryExponentPartRequired, linnum, p - 1, 1);
					}
					break goto_done;

				case 5: // looking immediately to right of E
					dblstate++;
					if (c == '-' || c == '+')
						break;
				case 6: // 1st exponent digit expected
					if (!Chars.isdigit(c)) {
						error("Exponent expected",
								IProblem.ExponentExpected, linnum, p - 1, 1);
					}
					dblstate++;
					break;

				case 8: // past end of exponent digits
					break goto_done;
				}
				break;
			}
			if (writeByte) {
				stringbuffer.writeByte(c);
			}
			fullNumber.append((char) c); 
		}
		p--;

		/*
		 * #if _WIN32 && __DMC__ char *save = __locale_decpoint;
		 * __locale_decpoint = "."; #endif #ifdef IN_GCC t->float80value =
		 * real_t::parse((char *)stringbuffer.data, real_t::LongDouble); #else
		 * t->float80value = strtold((char *)stringbuffer.data, NULL); #endif
		 * errno = 0;
		 */
		switch (input[p]) {
		case 'F':
		case 'f':
			/*
			 * #ifdef IN_GCC real_t::parse((char *)stringbuffer.data,
			 * real_t::Float); #else strtof((char *)stringbuffer.data, NULL);
			 * #endif
			 */
			result = TOKfloat32v;
			fullNumber.append(input[p]);
			p++;
			break;

		default:
			/*
			 * #ifdef IN_GCC real_t::parse((char *)stringbuffer.data,
			 * real_t::Double); #else strtod((char *)stringbuffer.data, NULL);
			 * #endif
			 */
			result = TOKfloat64v;
			break;

		case 'l':
			// if (!global.params.useDeprecated)
			error("'l' suffix is deprecated, use 'L' instead",
					IProblem.LSuffixDeprecated,
					linnum, p, 1);
		case 'L':
			result = TOKfloat80v;
			fullNumber.append(input[p]);
			p++;
			break;
		}
		if (input[p] == 'i' || input[p] == 'I') {
			if (input[p] == 'I') {
				error("'I' suffix is deprecated, use 'i' instead",
						IProblem.ISuffixDeprecated,
						linnum, p, 1);
			}
			fullNumber.append(input[p]);
			p++;
			switch (result) {
			case TOKfloat32v:
				result = TOKimaginary32v;
				break;
			case TOKfloat64v:
				result = TOKimaginary64v;
				break;
			case TOKfloat80v:
				result = TOKimaginary80v;
				break;
			}
		}
		/*
		 * #if _WIN32 && __DMC__ __locale_decpoint = save; #endif if
		 * (errno == ERANGE) error("number is not representable");
		 */
		
		t.string = new String(fullNumber);
		
		return result;
	}
	
	private void pragma(Token t) {
		boolean stay = true;
		boolean isRN = false;
		do {
			switch(input[p]) {
			case 0:
			case 0x1A:
				p++;
				stay = false;
				break;
			case '\n':
				newline(NOT_IN_COMMENT);
				p++;				
				stay = false;
				break;
			case '\r':
				p++;
				if (input[p] == '\n') {
					newline(NOT_IN_COMMENT);
					p++;					
					stay = false;
					isRN = true;
				}
				break;
			default:
				// TODO make the PS and LS stuff
				p++;
			}
		} while(stay);
		t.len = p - t.ptr - (isRN ? 2 : 1);
		t.value = TOKPRAGMA;
		t.string = new String(input, t.ptr, t.len);
	}
	
	private void whitespace(Token t) {
		boolean stay = true;
		do {
			switch(input[p]) {
			case ' ':
			case '\t':
			case '\f':
				p++;
				break;
			case '\n':
				newline(NOT_IN_COMMENT);
				p++;				
				break;
			case '\r':
				p++;
				if (input[p] == '\n') {
					newline(NOT_IN_COMMENT);
					p++;					
					break;
				}
				break;
			default:
				// TODO make the PS and LS stuff
				stay = false;
			}
		} while(stay);
		t.len = p - t.ptr;
		t.value = TOKwhitespace;
		t.string = new String(input, t.ptr, t.len);
	}
	
	private int decodeUTF() {
		try { 
		 	// decode one codepoint, starting at the index p 
		 	int result = Character.codePointAt(input, p); 
		 	// increase p with the count of chars for the decoded codepoint. 
		 	p = Character.offsetByCodePoints(input, 0, input.length, p, 1); 
		 	return result; 
		 } catch (Exception e) { 
		 	// a problem while decoding the codepoint occured => invalid input 
		 	error("invalid input sequence", IProblem.InvalidUtf8Sequence, linnum, p, 1); 
		 	return 0; 
		 } 
	}
	
	/*
	private void getDocComment(Token t, boolean lineComment) {
		OutBuffer buf = new OutBuffer();
	    char ct = input[t.ptr + 2];
	    int q = t.ptr + 3;	// start of comment text
	    int linestart = 0;

	    int qend = p;
	    if (ct == '*' || ct == '+')
		qend -= 2;

	    for (; q < qend; q++)
	    {
		if (input[q] != ct)
		    break;
	    }

	    if (ct != '/')
	    {
		for (; q < qend; qend--)
		{
		    if (input[qend - 1] != ct)
			break;
		}
	    }

	    for (; q < qend; q++)
	    {
	    	char c = input[q];

		switch (c)
		{
		    case '*':
		    case '+':
			if (linestart != 0 && c == ct)
			{   linestart = 0;
			    while (buf.data.length() > 0 && (buf.data.charAt(buf.data.length() - 1) == ' ' || buf.data.charAt(buf.data.length() - 1) == '\t'))
				buf.data.deleteCharAt(buf.data.length() - 1);
			    continue;
			}
			break;

		    case ' ':
		    case '\t':
			break;

		    case '\r':
			if (input[q + 1] == '\n')
			    continue;		// skip the \r
			
				c = '\n';		// replace all newlines with \n
				linestart = 1;

				while (buf.data.length() > 0 && (buf.data.charAt(buf.data.length() - 1) == ' ' || buf.data.charAt(buf.data.length() - 1) == '\t'))
					buf.data.deleteCharAt(buf.data.length() - 1);
				break;

		    default:
			if (c == 226)
			{
			    // If LS or PS
			    if (input[q + 1] == 128 &&
				(input[q + 2] == 168 || input[q + 2] == 169))
			    {
				q += 2;
				c = '\n';		// replace all newlines with \n
				linestart = 1;

				while (buf.data.length() > 0 && (buf.data.charAt(buf.data.length() - 1) == ' ' || buf.data.charAt(buf.data.length() - 1) == '\t'))
					buf.data.deleteCharAt(buf.data.length() - 1);
				break;
			    }
			}
			linestart = 0;
			break;

		    case '\n':
			linestart = 1;

			while (buf.data.length() > 0 && (buf.data.charAt(buf.data.length() - 1) == ' ' || buf.data.charAt(buf.data.length() - 1) == '\t'))
				buf.data.deleteCharAt(buf.data.length() - 1);

			break;
		}
		buf.writeByte(c);
	    }

	    // Always end with a newline
	    if (buf.data.length() == 0 || buf.data.charAt(buf.data.length() - 1) != '\n')
		buf.writeByte('\n');

	    // It's a line comment if the start of the doc comment comes
	    // after other non-whitespace on the same line.
	    if (lineComment && anyToken) {
	    	if (t.lineComment != null) {
	    		t.blockCommentLength = p - 1;
	    		t.lineComment = combineComments(t.lineComment, buf.data.toString());
	    	} else {
	    		t.lineCommentPtr = t.ptr;
	    		t.lineCommentLength = p - 1;
	    		t.lineComment = buf.data.toString();	    		
	    		buf.reset();
	    	}
	    } else {
	    	if (t.ustring != null) {
	    		t.blockCommentLength = p - 1;
	    		t.comment = combineComments(t.comment, buf.data.toString());
	    	} else {
	    		t.blockCommentPtr = t.ptr;
	    		t.blockCommentLength = p - 1;
	    		t.comment = buf.data.toString().trim();
	    		buf.reset();
	    	}
	    }
	}
	
	public String combineComments(String c1, String c2) {
		StringBuilder sb = new StringBuilder();
		if (c1 != null) {
			sb.append(c1.trim());
			if (c2 != null) {
				sb.append("\n\n");
				sb.append(c2.trim());
			}
		}
		return sb.toString();
	}
	
	
	public Identifier idPool(String s) {
	    Identifier id;
	    StringValue sv;

	    sv = stringtable.update(s);
	    id = (Identifier) sv.ptrvalue;
	    if (id == null)
	    {
	    	id = new Identifier(sv.lstring, TOKidentifier);
	    	sv.ptrvalue = id;
	    }
	    return id;
	}
	*/
	
	private static Map<String, TOK> keywordsD2;
	private static Map<String, TOK> keywordsD1;
	
	static {
		keywordsD2 = new HashMap<String, TOK>();
		keywordsD2.put("this", TOKthis);
		keywordsD2.put("super", TOKsuper);
		keywordsD2.put("assert", TOKassert);
		keywordsD2.put("null", TOKnull);
		keywordsD2.put("true", TOKtrue);
		keywordsD2.put("false", TOKfalse);
		keywordsD2.put("cast", TOKcast);
		keywordsD2.put("new", TOKnew);
		keywordsD2.put("delete", TOKdelete);
		keywordsD2.put("throw", TOKthrow);
		keywordsD2.put("module", TOKmodule);
		keywordsD2.put("pragma", TOKpragma);
		keywordsD2.put("typeof", TOKtypeof);
		keywordsD2.put("typeid", TOKtypeid);
		keywordsD2.put("template", TOKtemplate);
		keywordsD2.put("void", TOKvoid);
		keywordsD2.put("byte", TOKint8);
		keywordsD2.put("ubyte", TOKuns8);

		keywordsD2.put("short", TOKint16);
		keywordsD2.put("ushort", TOKuns16);
		keywordsD2.put("int",	 TOKint32);
		keywordsD2.put("uint", TOKuns32);
		keywordsD2.put("long", TOKint64);
		keywordsD2.put("ulong", TOKuns64);
	    keywordsD2.put("cent", TOKcent);
	    keywordsD2.put("ucent", TOKucent);
	    keywordsD2.put("float", TOKfloat32);
	    keywordsD2.put("double", TOKfloat64);
	    keywordsD2.put("real", TOKfloat80);

	    //keywordsD2.put("bit", TOKbit);
	    keywordsD2.put("bool", TOKbool);
	    keywordsD2.put("char", TOKchar);
	    keywordsD2.put("wchar", TOKwchar);
	    keywordsD2.put("dchar", TOKdchar);

	    keywordsD2.put("ifloat", TOKimaginary32);
	    keywordsD2.put("idouble", TOKimaginary64);
	    keywordsD2.put("ireal", TOKimaginary80);

	    keywordsD2.put("cfloat", TOKcomplex32);
	    keywordsD2.put("cdouble", TOKcomplex64);
	    keywordsD2.put("creal", TOKcomplex80);

	    keywordsD2.put("delegate", TOKdelegate);
	    keywordsD2.put("function", TOKfunction);

	    keywordsD2.put("is", TOKis);
	    keywordsD2.put("if", TOKif);
	    keywordsD2.put("else", TOKelse);
	    keywordsD2.put("while", TOKwhile);
	    keywordsD2.put("for", TOKfor);
	    keywordsD2.put("do", TOKdo);
	    keywordsD2.put("switch", TOKswitch);
	    keywordsD2.put("case", TOKcase);
	    keywordsD2.put("default", TOKdefault);
	    keywordsD2.put("break", TOKbreak);
	    keywordsD2.put("continue", TOKcontinue);
	    keywordsD2.put("synchronized", TOKsynchronized);
	    keywordsD2.put("return", TOKreturn);
	    keywordsD2.put("goto", TOKgoto);
	    keywordsD2.put("try", TOKtry);
	    keywordsD2.put("catch", TOKcatch);
	    keywordsD2.put("finally", TOKfinally);
	    keywordsD2.put("with", TOKwith);
	    keywordsD2.put("asm", TOKasm);
	    keywordsD2.put("foreach", TOKforeach);
	    keywordsD2.put("foreach_reverse", TOKforeach_reverse);
	    keywordsD2.put("scope", TOKscope);

	    keywordsD2.put("struct", TOKstruct);
	    keywordsD2.put("class", TOKclass);
	    keywordsD2.put("interface", TOKinterface);
	    keywordsD2.put("union", TOKunion);
	    keywordsD2.put("enum", TOKenum);
	    keywordsD2.put("import", TOKimport);
	    keywordsD2.put("mixin", TOKmixin);
	    keywordsD2.put("static", TOKstatic);
	    keywordsD2.put("final", TOKfinal);
	    keywordsD2.put("const", TOKconst);
	    keywordsD2.put("typedef", TOKtypedef);
	    keywordsD2.put("alias", TOKalias);
	    keywordsD2.put("override", TOKoverride);
	    keywordsD2.put("abstract", TOKabstract);
	    keywordsD2.put("volatile", TOKvolatile);
	    keywordsD2.put("debug", TOKdebug);
	    keywordsD2.put("deprecated", TOKdeprecated);
	    keywordsD2.put("in", TOKin);
	    keywordsD2.put("out", TOKout);
	    keywordsD2.put("inout", TOKinout);
	    keywordsD2.put("lazy", TOKlazy);
	    keywordsD2.put("auto", TOKauto);

	    keywordsD2.put("align", TOKalign);
	    keywordsD2.put("extern", TOKextern);
	    keywordsD2.put("private", TOKprivate);
	    keywordsD2.put("package", TOKpackage);
	    keywordsD2.put("protected", TOKprotected);
	    keywordsD2.put("public", TOKpublic);
	    keywordsD2.put("export", TOKexport);

	    keywordsD2.put("body", TOKbody	);
	    keywordsD2.put("invariant", TOKinvariant);
	    keywordsD2.put("unittest", TOKunittest);
	    keywordsD2.put("version", TOKversion);
	    
	    keywordsD1 = new HashMap<String, TOK>();
	    keywordsD1.putAll(keywordsD2);
	    keywordsD1.put("iftype", TOKiftype);
	    keywordsD1.put("on_scope_exit", TOKon_scope_exit);
	    keywordsD1.put("on_scope_failure", TOKon_scope_failure);
	    keywordsD1.put("on_scope_success", TOKon_scope_success);
	}
	
	private void initKeywords() {
		StringValue sv;
		Map<String, TOK> keys;
		if (apiLevel == AST.D1) {
			keys = keywordsD1;
		} else if (apiLevel == AST.D2) {
			keys = keywordsD2;
		} else {
			throw new IllegalStateException();
		}
		for(Map.Entry<String, TOK> entry : keys.entrySet()) {
			sv = stringtable.update(entry.getKey());
			if (sv.ptrvalue == null) {
				sv.ptrvalue = new Identifier(sv.lstring, entry.getValue());
			} else {
				((Identifier) sv.ptrvalue).value = entry.getValue();
			}			
		}
	}
	
	private void initId() {
		try {
			Field[] idFields = Id.class.getFields();			
			StringValue sv;
			for(Field idField : idFields) {
				Identifier id = (Identifier) idField.get(null);
				sv = stringtable.update(id.string);
				sv.ptrvalue = id;
			}
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Bug!");
		}
	}	
	
	protected void newline(boolean inComment) {
		if (recordLineSeparator && linnum - 1 == lineEnds.size()) {
			lineEnds.add(p);
		}
		linnum++;		
	}
	
	protected void setMalformed(ASTNode node) {
		node.astFlags |= descent.core.dom.ASTNode.MALFORMED;;
	}
	
	protected void setRecovered(ASTNode node) {
		node.astFlags |= descent.core.dom.ASTNode.MALFORMED;
	}
	
	protected void setMalformed(descent.core.dom.ASTNode node) {
		node.setFlags(node.getFlags() | descent.core.dom.ASTNode.MALFORMED);
	}
	
	protected void setRecovered(descent.core.dom.ASTNode node) {
		node.setFlags(node.getFlags() | descent.core.dom.ASTNode.RECOVERED);
	}
	
	public int[] getLineEnds() {
		int[] ends = new int[lineEnds.size()];
		for(int i = 0; i < lineEnds.size(); i++) {
			ends[i] = lineEnds.get(i);
		}
		return ends;
	}
	
	// IProblemRequestor members:
	
	public void acceptProblem(IProblem problem) {
		problems.add(problem);
	}
	
	public boolean isActive() {
		return true;
	}
	
	public void beginReporting() {
	}
	
	public void endReporting() {
	}

}
