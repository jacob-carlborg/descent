package descent.internal.compiler.parser;

import static descent.internal.compiler.parser.TOK.TOKPRAGMA;
import static descent.internal.compiler.parser.TOK.TOKadd;
import static descent.internal.compiler.parser.TOK.TOKaddass;
import static descent.internal.compiler.parser.TOK.TOKand;
import static descent.internal.compiler.parser.TOK.TOKandand;
import static descent.internal.compiler.parser.TOK.TOKandass;
import static descent.internal.compiler.parser.TOK.TOKassign;
import static descent.internal.compiler.parser.TOK.TOKblockcomment;
import static descent.internal.compiler.parser.TOK.TOKcatass;
import static descent.internal.compiler.parser.TOK.TOKcharv;
import static descent.internal.compiler.parser.TOK.TOKcolon;
import static descent.internal.compiler.parser.TOK.TOKcomma;
import static descent.internal.compiler.parser.TOK.TOKdcharv;
import static descent.internal.compiler.parser.TOK.TOKdiv;
import static descent.internal.compiler.parser.TOK.TOKdivass;
import static descent.internal.compiler.parser.TOK.TOKdocblockcomment;
import static descent.internal.compiler.parser.TOK.TOKdoclinecomment;
import static descent.internal.compiler.parser.TOK.TOKdocpluscomment;
import static descent.internal.compiler.parser.TOK.TOKdot;
import static descent.internal.compiler.parser.TOK.TOKdotdotdot;
import static descent.internal.compiler.parser.TOK.TOKeof;
import static descent.internal.compiler.parser.TOK.TOKequal;
import static descent.internal.compiler.parser.TOK.TOKfloat32v;
import static descent.internal.compiler.parser.TOK.TOKfloat64v;
import static descent.internal.compiler.parser.TOK.TOKfloat80v;
import static descent.internal.compiler.parser.TOK.TOKge;
import static descent.internal.compiler.parser.TOK.TOKgt;
import static descent.internal.compiler.parser.TOK.TOKidentity;
import static descent.internal.compiler.parser.TOK.TOKimaginary32v;
import static descent.internal.compiler.parser.TOK.TOKimaginary64v;
import static descent.internal.compiler.parser.TOK.TOKimaginary80v;
import static descent.internal.compiler.parser.TOK.TOKint32v;
import static descent.internal.compiler.parser.TOK.TOKint64v;
import static descent.internal.compiler.parser.TOK.TOKlbracket;
import static descent.internal.compiler.parser.TOK.TOKlcurly;
import static descent.internal.compiler.parser.TOK.TOKle;
import static descent.internal.compiler.parser.TOK.TOKleg;
import static descent.internal.compiler.parser.TOK.TOKlg;
import static descent.internal.compiler.parser.TOK.TOKlinecomment;
import static descent.internal.compiler.parser.TOK.TOKlparen;
import static descent.internal.compiler.parser.TOK.TOKlt;
import static descent.internal.compiler.parser.TOK.TOKmin;
import static descent.internal.compiler.parser.TOK.TOKminass;
import static descent.internal.compiler.parser.TOK.TOKminusminus;
import static descent.internal.compiler.parser.TOK.TOKmod;
import static descent.internal.compiler.parser.TOK.TOKmodass;
import static descent.internal.compiler.parser.TOK.TOKmul;
import static descent.internal.compiler.parser.TOK.TOKmulass;
import static descent.internal.compiler.parser.TOK.TOKnot;
import static descent.internal.compiler.parser.TOK.TOKnotequal;
import static descent.internal.compiler.parser.TOK.TOKnotidentity;
import static descent.internal.compiler.parser.TOK.TOKor;
import static descent.internal.compiler.parser.TOK.TOKorass;
import static descent.internal.compiler.parser.TOK.TOKoror;
import static descent.internal.compiler.parser.TOK.TOKpluscomment;
import static descent.internal.compiler.parser.TOK.TOKplusplus;
import static descent.internal.compiler.parser.TOK.TOKquestion;
import static descent.internal.compiler.parser.TOK.TOKrbracket;
import static descent.internal.compiler.parser.TOK.TOKrcurly;
import static descent.internal.compiler.parser.TOK.TOKrparen;
import static descent.internal.compiler.parser.TOK.TOKsemicolon;
import static descent.internal.compiler.parser.TOK.TOKshl;
import static descent.internal.compiler.parser.TOK.TOKshlass;
import static descent.internal.compiler.parser.TOK.TOKshr;
import static descent.internal.compiler.parser.TOK.TOKshrass;
import static descent.internal.compiler.parser.TOK.TOKslice;
import static descent.internal.compiler.parser.TOK.TOKstring;
import static descent.internal.compiler.parser.TOK.TOKtilde;
import static descent.internal.compiler.parser.TOK.TOKue;
import static descent.internal.compiler.parser.TOK.TOKug;
import static descent.internal.compiler.parser.TOK.TOKuge;
import static descent.internal.compiler.parser.TOK.TOKul;
import static descent.internal.compiler.parser.TOK.TOKule;
import static descent.internal.compiler.parser.TOK.TOKunord;
import static descent.internal.compiler.parser.TOK.TOKuns32v;
import static descent.internal.compiler.parser.TOK.TOKuns64v;
import static descent.internal.compiler.parser.TOK.TOKushr;
import static descent.internal.compiler.parser.TOK.TOKushrass;
import static descent.internal.compiler.parser.TOK.TOKwcharv;
import static descent.internal.compiler.parser.TOK.TOKwhitespace;
import static descent.internal.compiler.parser.TOK.TOKxor;
import static descent.internal.compiler.parser.TOK.TOKxorass;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import descent.core.IProblemRequestor;
import descent.core.compiler.CharOperation;
import descent.core.compiler.IProblem;
import descent.core.dom.AST;

/**
 * Internal lexer class.
 */
public class Lexer implements IProblemRequestor {
	
	private static final int[] EMPTY_LINE_ENDS = new int[0];
	
	private final static boolean IN_COMMENT = true;
	private final static boolean NOT_IN_COMMENT = false;
	
	private final static BigInteger X_FFFFFFFFFFFFFFFF =     new BigInteger("FFFFFFFFFFFFFFFF", 16);
	private final static BigInteger X_8000000000000000 =         new BigInteger("8000000000000000", 16);
	private final static BigInteger X_FFFFFFFF00000000  =       new BigInteger("FFFFFFFF00000000", 16);
	private final static BigInteger X_80000000 =                 new BigInteger("80000000", 16);
	private final static BigInteger X_FFFFFFFF80000000 =     new BigInteger("FFFFFFFF80000000", 16);
	                                                                       
	private final static int LS = 0x2028;
	private final static int PS = 0x2029;
	
	private OutBuffer stringbuffer = new OutBuffer();
	private Token freelist;
	
	public int base;
	public int p;
	public int end;
	public char[] input;
	public Loc loc;
	
	public Token token;
	public Token prevToken = new Token();
	
	public List<IProblem> problems;
	
	// support for the  poor-line-debuggers ....
	// remember the position of the cr/lf
	private int[] lineEnds;
	private int linnum = 1;
	private int maxLinnum = 1;
	
	// task tag support
	public char[][] foundTaskTags = null;
	public char[][] foundTaskMessages;
	public char[][] foundTaskPriorities = null;
	public int[][] foundTaskPositions;
	public int foundTaskCount = 0;
	public char[][] taskTags = null;
	public char[][] taskPriorities = null;
	public boolean isTaskCaseSensitive = true;
	
	private boolean tokenizeComments;
	public boolean tokenizeWhiteSpace;
	private boolean tokenizePragmas;
	private boolean recordLineSeparator;
	protected final int apiLevel;
	
	/* package */ Lexer(int apiLevel) {
		this.apiLevel = apiLevel;
		//initId();
		//initKeywords();		
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
		this.lineEnds = new int[250];
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
	    int curlynest = 0;
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
					
			    case TOKlcurly:
					curlynest++;
					continue;

				    case TOKrcurly:
					if (--curlynest >= 0)
					    continue;
					break;

				    case TOKsemicolon:
					if (curlynest != 0)
					    continue;
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
			t.string = new String(input, t.ptr, t.len);
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
				
				if (this.taskTags != null) {
					checkTaskTag(t.ptr, p);
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
						if (this.taskTags != null) {
							checkTaskTag(t.ptr, p);
						}
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
				
				if (this.taskTags != null) {
					checkTaskTag(t.ptr, p);
				}
				if (tokenizeComments) {
					p++;
					
					t.value = input[t.ptr + 2] == '/' ? TOKdoclinecomment : TOKlinecomment;
					t.len = p - t.ptr;
					t.string = new String(input, t.ptr, t.len);
					
					newline(IN_COMMENT);
					return;
				} else {
					p++;
					
					newline(IN_COMMENT);
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
				
				if (this.taskTags != null) {
					checkTaskTag(t.ptr, p);
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
			    t.string = new String(input, t.ptr, t.len);
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
			    if (input[p] == '=' && apiLevel == AST.D0)
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
			    if (input[p] == '=' && apiLevel == AST.D0)
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
		switch(input[p]) {
		// a:
		case 'a':
			p++;
			switch(input[p]) {
			case 'b':
				p++;
				if (input[p] == 's') {
					p++;
					if (input[p] == 't') {
						p++;
						if (input[p] == 'r') {
							p++;
							if (input[p] == 'a') {
								p++;
								if (input[p] == 'c') {
									p++;
									if (input[p] == 't' && !Chars.isidchar(input[p+1])) {
										t.value = TOK.TOKabstract;
										t.len = 8;
										p++;
										return;
									}
								}
							}
						}
					}
				}
				break;
			case 'l':
				p++;
				if (input[p] == 'i') {
					p++;
					switch(input[p]) {
					case 'a':
						p++;
						if (input[p] == 's' && !Chars.isidchar(input[p+1])) {
							t.value = TOK.TOKalias;
							t.len = 5;
							p++;
							return;
						}
						break;
					case 'g':
						p++;
						if (input[p] == 'n' && !Chars.isidchar(input[p+1])) {
							t.value = TOK.TOKalign;
							t.len = 5;
							p++;
							return;
						}
						break;
					}
				}
				break;
			case 's':
				p++;
				switch(input[p]) {
				case 'm':
					if (!Chars.isidchar(input[p+1])) {
						t.value = TOK.TOKasm;
						t.len = 3;
						p++;
						return;
					}
					break;
				case 's':
					p++;
					if (input[p] == 'e') {
						p++;
						if (input[p] == 'r') {
							p++;
							if (input[p] == 't' && !Chars.isidchar(input[p+1])) {
								t.value = TOK.TOKassert;
								t.len = 6;
								p++;
								return;
							}
						}
					}
					break;
				}
				break;
			case 'u':
				p++;
				if (input[p] == 't') {
					p++;
					if (input[p] == 'o' && !Chars.isidchar(input[p+1])) {
						t.value = TOK.TOKauto;
						t.len = 4;
						p++;
						return;
					}
				}
				break;
			}
			break;
		// b:
		case 'b':
			p++;
			switch(input[p]) {
			case 'o':
				p++;
				switch(input[p]) {
				case 'd':
					p++;
					if (input[p] == 'y' && !Chars.isidchar(input[p+1])) {
						t.value = TOK.TOKbody;
						t.len = 4;
						p++;
						return;
					}
					break;
				case 'o':
					p++;
					if (input[p] == 'l' && !Chars.isidchar(input[p+1])) {
						t.value = TOK.TOKbool;
						t.len = 4;
						p++;
						return;
					}
					break;
				}
				break;
			case 'r':
				p++;
				if (input[p] == 'e') {
					p++;
					if (input[p] == 'a') {
						p++;
						if (input[p] == 'k' && !Chars.isidchar(input[p+1])) {
							t.value = TOK.TOKbreak;
							t.len = 5;
							p++;
							return;
						}
					}
				}
				break;
			case 'y':
				p++;
				if (input[p] == 't') {
					p++;
					if (input[p] == 'e' && !Chars.isidchar(input[p+1])) {
						t.value = TOK.TOKint8;
						t.len = 4;p++;
						return;
					}
				}
				break;
			}
			break;
		// c:
		case 'c':
			p++;
			switch(input[p]) {
			case 'a':
				p++;
				switch(input[p]) {
				case 's':
					p++;
					switch(input[p]) {
					case 'e':
						if (!Chars.isidchar(input[p+1])) {
							t.value = TOK.TOKcase;
							t.len = 4;
							p++;
							return;
						}
						break;
					case 't':
						if (!Chars.isidchar(input[p+1])) {
							t.value = TOK.TOKcast;
							t.len = 4;
							p++;
							return;
						}
						break;
					}
					break;
				case 't':
					p++;
					if (input[p] == 'c') {
						p++;
						if (input[p] == 'h' && !Chars.isidchar(input[p+1])) {
							t.value = TOK.TOKcatch;
							t.len = 5;
							p++;
							return;
						}
					}
					break;
				}
				break;
			case 'e':
				p++;
				if (input[p] == 'n') {
					p++;
					if (input[p] == 't' && !Chars.isidchar(input[p+1])) {
						t.value = TOK.TOKcent;
						t.len = 4;
						p++;
						return;
					}
				}
				break;
			case 'd':
				p++;
				if (input[p] == 'o') {
					p++;
					if (input[p] == 'u') {
						p++;
						if (input[p] == 'b') {
							p++;
							if (input[p] == 'l') {
								p++;
								if (input[p] == 'e' && !Chars.isidchar(input[p+1])) {
									t.value = TOK.TOKcomplex64;
									t.len = 7;
									p++;
									return;
								}
							}
						}
					}
				}
				break;
			case 'f':
				p++;
				if (input[p] == 'l') {
					p++;
					if (input[p] == 'o') {
						p++;
						if (input[p] == 'a') {
							p++;
							if (input[p] == 't' && !Chars.isidchar(input[p+1])) {
								t.value = TOK.TOKcomplex32;
								t.len = 6;
								p++;
								return;
							}
						}
					}
				}
				break;
			case 'h':
				p++;
				if (input[p] == 'a') {
					p++;
					if (input[p] == 'r' && !Chars.isidchar(input[p+1])) {
						t.value = TOK.TOKchar;
						t.len = 4;
						p++;
						return;
					}
				}
				break;
			case 'l':
				p++;
				if (input[p] == 'a') {
					p++;
					if (input[p] == 's') {
						p++;
						if (input[p] == 's' && !Chars.isidchar(input[p+1])) {
							t.value = TOK.TOKclass;
							t.len = 5;
							p++;
							return;
						}
					}
				}
				break;
			case 'o':
				p++;
				if (input[p] == 'n') {
					p++;
					switch(input[p]) {
					case 's':
						p++;
						if (input[p] == 't' && !Chars.isidchar(input[p+1])) {
							t.value = TOK.TOKconst;
							t.len = 5;
							p++;
							return;
						}
						break;
					case 't':
						p++;
						if (input[p] == 'i') {
							p++;
							if (input[p] == 'n') {
								p++;
								if (input[p] == 'u') {
									p++;
									if (input[p] == 'e' && !Chars.isidchar(input[p+1])) {
										t.value = TOK.TOKcontinue;
										t.len = 8;
										p++;
										return;
									}
								}
							}
						}
						break;
					}
				}
				break;
			case 'r':
				p++;
				if (input[p] == 'e') {
					p++;
					if (input[p] == 'a') {
						p++;
						if (input[p] == 'l' && !Chars.isidchar(input[p+1])) {
							t.value = TOK.TOKcomplex80;
							t.len = 5;
							p++;
							return;
						}
					}
				}
				break;
			}
			break;
		// d:
		case 'd':
			p++;
			switch(input[p]) {
			case 'c':
				p++;
				if (input[p] == 'h') {
					p++;
					if (input[p] == 'a') {
						p++;
						if (input[p] == 'r' && !Chars.isidchar(input[p+1])) {
							t.value = TOK.TOKdchar;
							t.len = 5;
							p++;
							return;
						}
					}
				}
				break;
			case 'e':
				p++;
				switch(input[p]) {
				case 'b':
					p++;
					if (input[p] == 'u') {
						p++;
						if (input[p] == 'g' && !Chars.isidchar(input[p+1])) {
							t.value = TOK.TOKdebug;
							t.len = 5;
							p++;
							return;
						}
					}
					break;
				case 'f':
					p++;
					if (input[p] == 'a') {
						p++;
						if (input[p] == 'u') {
							p++;
							if (input[p] == 'l') {
								p++;
								if (input[p] == 't' && !Chars.isidchar(input[p+1])) {
									t.value = TOK.TOKdefault;
									t.len = 7;
									p++;
									return;
								}
							}
						}
					}
					break;
				case 'l':
					p++;
					if (input[p] == 'e') {
						p++;
						switch(input[p]) {
						case 'g':
							p++;
							if (input[p] == 'a') {
								p++;
								if (input[p] == 't') {
									p++;
									if (input[p] == 'e' && !Chars.isidchar(input[p+1])) {
										t.value = TOK.TOKdelegate;
										t.len = 8;
										p++;
										return;
									}
								}
							}
							break;
						case 't':
							p++;
							if (input[p] == 'e' && !Chars.isidchar(input[p+1])) {
								t.value = TOK.TOKdelete;
								t.len = 6;
								p++;
								return;
							}
							break;
						}
					}
					break;
				case 'p':
					p++;
					if (input[p] == 'r') {
						p++;
						if (input[p] == 'e') {
							p++;
							if (input[p] == 'c') {
								p++;
								if (input[p] == 'a') {
									p++;
									if (input[p] == 't') {
										p++;
										if (input[p] == 'e') {
											p++;
											if (input[p] == 'd' && !Chars.isidchar(input[p+1])) {
												t.value = TOK.TOKdeprecated;
												t.len = 10;
												p++;
												return;
											}
										}
									}
								}
							}
						}
					}
					break;
				}
				break;
			case 'o':
				if (!Chars.isidchar(input[p+1])) {
					t.value = TOK.TOKdo;
					t.len = 2;
					p++;
					return;
				}
				
				p++;
				if (input[p] == 'u') {
					p++;
					if (input[p] == 'b') {
						p++;
						if (input[p] == 'l') {
							p++;
							if (input[p] == 'e' && !Chars.isidchar(input[p+1])) {
								t.value = TOK.TOKfloat64;
								t.len = 6;
								p++;
								return;
							}
						}
					}
				}
				break;
			}
			break;
		// e:
		case 'e':
			p++;
			switch(input[p]) {
			case 'l':
				p++;
				if (input[p] == 's') {
					p++;
					if (input[p] == 'e' && !Chars.isidchar(input[p+1])) {
						t.value = TOK.TOKelse;
						t.len = 4;
						p++;
						return;
					}
				}
				break;
			case 'n':
				p++;
				if (input[p] == 'u') {
					p++;
					if (input[p] == 'm' && !Chars.isidchar(input[p+1])) {
						t.value = TOK.TOKenum;
						t.len = 4;
						p++;
						return;
					}
				}
				break;
			case 'x':
				p++;
				switch(input[p]) {
				case 'p':
					p++;
					if (input[p] == 'o') {
						p++;
						if (input[p] == 'r') {
							p++;
							if (input[p] == 't' && !Chars.isidchar(input[p+1])) {
								t.value = TOK.TOKexport;
								t.len = 6;
								p++;
								return;
							}
						}
					}
					break;
				case 't':
					p++;
					if (input[p] == 'e') {
						p++;
						if (input[p] == 'r') {
							p++;
							if (input[p] == 'n' && !Chars.isidchar(input[p+1])) {
								t.value = TOK.TOKextern;
								t.len = 6;
								p++;
								return;
							}
						}
					}
					break;
				}
				break;
			}
			break;
		// f:
		case 'f':
			p++;
			switch(input[p]) {
			case 'a':
				p++;
				if (input[p] == 'l') {
					p++;
					if (input[p] == 's') {
						p++;
						if (input[p] == 'e' && !Chars.isidchar(input[p+1])) {
							t.value = TOK.TOKfalse;
							t.len = 5;
							p++;
							return;
						}
					}
				}
				break;
			case 'i':
				p++;
				if (input[p] == 'n') {
					p++;
					if (input[p] == 'a') {
						p++;
						if (input[p] == 'l') {
							if (!Chars.isidchar(input[p+1])) {
								t.value = TOK.TOKfinal;
								t.len = 5;
								p++;
								return;
							}
							
							p++;
							if (input[p] == 'l') {
								p++;
								if (input[p] == 'y' && !Chars.isidchar(input[p+1])) {
									t.value = TOK.TOKfinally;
									t.len = 7;
									p++;
									return;
								}
							}
						}
					}
				}
				break;
			case 'l':
				p++;
				if (input[p] == 'o') {
					p++;
					if (input[p] == 'a') {
						p++;
						if (input[p] == 't' && !Chars.isidchar(input[p+1])) {
							t.value = TOK.TOKfloat32;
							t.len = 5;
							p++;
							return;
						}
					}
				}
				break;
			case 'o':
				p++;
				if (input[p] == 'r') {
					if (!Chars.isidchar(input[p+1])) {
						t.value = TOK.TOKfor;
						t.len = 3;
						p++;
						return;
					}
					
					p++;
					if (input[p] == 'e') {
						p++;
						if (input[p] == 'a') {
							p++;
							if (input[p] == 'c') {
								p++;
								if (input[p] == 'h') {
									if (!Chars.isidchar(input[p+1])) {
										t.value = TOK.TOKforeach;
										t.len = 7;
										p++;
										return;
									}
									
									p++;
									if (input[p] == '_') {
										p++;
										if (input[p] == 'r') {
											p++;
											if (input[p] == 'e') {
												p++;
												if (input[p] == 'v') {
													p++;
													if (input[p] == 'e') {
														p++;
														if (input[p] == 'r') {
															p++;
															if (input[p] == 's') {
																p++;
																if (input[p] == 'e' && !Chars.isidchar(input[p+1])) {
																	t.value = TOK.TOKforeach_reverse;
																	t.len = 15;
																	p++;
																	return;
																}
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
				break;
			case 'u':
				p++;
				if (input[p] == 'n') {
					p++;
					if (input[p] == 'c') {
						p++;
						if (input[p] == 't') {
							p++;
							if (input[p] == 'i') {
								p++;
								if (input[p] == 'o') {
									p++;
									if (input[p] == 'n' && !Chars.isidchar(input[p+1])) {
										t.value = TOK.TOKfunction;
										t.len = 8;
										p++;
										return;
									}
								}
							}
						}
					}
				}
				break;
			}
			break;
		// g:
		case 'g':
			p++;
			if (input[p] == 'o') {
				p++;
				if (input[p] == 't') {
					p++;
					if (input[p] == 'o' && !Chars.isidchar(input[p+1])) {
						t.value = TOK.TOKgoto;
						t.len = 4;
						p++;
						return;
					}
				}
			}
			break;
		// i:
		case 'i':
			p++;
			switch(input[p]) {
			case 'd':
				p++;
				if (input[p] == 'o') {
					p++;
					if (input[p] == 'u') {
						p++;
						if (input[p] == 'b') {
							p++;
							if (input[p] == 'l') {
								p++;
								if (input[p] == 'e' && !Chars.isidchar(input[p+1])) {
									t.value = TOK.TOKimaginary64;
									t.len = 7;
									p++;
									return;
								}
							}
						}
					}
				}
				break;
			case 'f':
				if (!Chars.isidchar(input[p+1])) {
					t.value = TOK.TOKif;
					t.len = 2;
					p++;
					return;
				}
				
				p++;
				if (input[p] == 'l') {
					p++;
					if (input[p] == 'o') {
						p++;
						if (input[p] == 'a') {
							p++;
							if (input[p] == 't' && !Chars.isidchar(input[p+1])) {
								t.value = TOK.TOKimaginary32;
								t.len = 6;
								p++;
								return;
							}
						}
					}
				} else if (apiLevel == AST.D0 && input[p] == 't') {
					p++;
					if (input[p] == 'y') {
						p++;
						if (input[p] == 'p') {
							p++;
							if (input[p] == 'e' && !Chars.isidchar(input[p+1])) {
								t.value = TOK.TOKiftype;
								t.len = 6;
								p++;
								return;
							}
						}
					}
				}
				break;
			case 'm':
				p++;
				if (input[p] == 'p') {
					p++;
					if (input[p] == 'o') {
						p++;
						if (input[p] == 'r') {
							p++;
							if (input[p] == 't' && !Chars.isidchar(input[p+1])) {
								t.value = TOK.TOKimport;
								t.len = 6;
								p++;
								return;
							}
						}
					}
				}
				break;
			case 'n':
				if (!Chars.isidchar(input[p+1])) {
					t.value = TOK.TOKin;
					t.len = 2;
					p++;
					return;
				}
				
				p++;
				switch(input[p]) {
				case 'o':
					p++;
					if (input[p] == 'u') {
						p++;
						if (input[p] == 't' && !Chars.isidchar(input[p+1])) {
							t.value = TOK.TOKinout;
							t.len = 5;
							p++;
							return;
						}
					}
					break;
				case 't':
					if (!Chars.isidchar(input[p+1])) {
						t.value = TOK.TOKint32;
						t.len = 3;
						p++;
						return;
					}
					
					p++;
					if (input[p] == 'e') {
						p++;
						if (input[p] == 'r') {
							p++;
							if (input[p] == 'f') {
								p++;
								if (input[p] == 'a') {
									p++;
									if (input[p] == 'c') {
										p++;
										if (input[p] == 'e' && !Chars.isidchar(input[p+1])) {
											t.value = TOK.TOKinterface;
											t.len = 9;
											p++;
											return;
										}
									}
								}
							}
						}
					}
					break;
				case 'v':
					p++;
					if (input[p] == 'a') {
						p++;
						if (input[p] == 'r') {
							p++;
							if (input[p] == 'i') {
								p++;
								if (input[p] == 'a') {
									p++;
									if (input[p] == 'n') {
										p++;
										if (input[p] == 't' && !Chars.isidchar(input[p+1])) {
											t.value = TOK.TOKinvariant;
											t.len = 9;
											p++;
											return;
										}
									}
								}
							}
						}
					}
					break;
				}
				break;
			case 'r':
				p++;
				if (input[p] == 'e') {
					p++;
					if (input[p] == 'a') {
						p++;
						if (input[p] == 'l' && !Chars.isidchar(input[p+1])) {
							t.value = TOK.TOKimaginary80;
							t.len = 5;
							p++;
							return;
						}
					}
				}
				break;
			case 's':
				if (!Chars.isidchar(input[p+1])) {
					t.value = TOK.TOKis;
					t.len = 2;
					p++;
					return;
				}
				break;
			}
			break;
		// l:
		case 'l':
			p++;
			switch(input[p]) {
			case 'a':
				p++;
				if (input[p] == 'z') {
					p++;
					if (input[p] == 'y' && !Chars.isidchar(input[p+1])) {
						t.value = TOK.TOKlazy;
						t.len = 4;
						p++;
						return;
					}
				}
				break;
			case 'o':
				p++;
				if (input[p] == 'n') {
					p++;
					if (input[p] == 'g' && !Chars.isidchar(input[p+1])) {
						t.value = TOK.TOKint64;
						t.len = 4;
						p++;
						return;
					}
				}
				break;
			}
			break;
		// m:
		case 'm':
			p++;
			switch(input[p]) {
			case 'a':
				p++;
				if (input[p] == 'c') {
					p++;
					if (input[p] == 'r') {
						p++;
						if (input[p] == 'o' && !Chars.isidchar(input[p+1])) {
							t.value = TOK.TOKmacro;
							t.len = 5;
							p++;
							return;
						}
					}
				}
				break;
			case 'i':
				p++;
				if (input[p] == 'x') {
					p++;
					if (input[p] == 'i') {
						p++;
						if (input[p] == 'n' && !Chars.isidchar(input[p+1])) {
							t.value = TOK.TOKmixin;
							t.len = 5;
							p++;
							return;
						}
					}
				}
				break;
			case 'o':
				p++;
				if (input[p] == 'd') {
					p++;
					if (input[p] == 'u') {
						p++;
						if (input[p] == 'l') {
							p++;
							if (input[p] == 'e' && !Chars.isidchar(input[p+1])) {
								t.value = TOK.TOKmodule;
								t.len = 6;
								p++;
								return;
							}
						}
					}
				}
				break;
			}
			break;
		// n:
		case 'n':
			p++;
			switch(input[p]) {
			case 'e':
				p++;
				if (input[p] == 'w' && !Chars.isidchar(input[p+1])) {
					t.value = TOK.TOKnew;
					t.len = 3;
					p++;
					return;
				}
				break;
			case 'u':
				p++;
				if (input[p] == 'l') {
					p++;
					if (input[p] == 'l' && !Chars.isidchar(input[p+1])) {
						t.value = TOK.TOKnull;
						t.len = 4;
						p++;
						return;
					}
				}
				break;
			}
			break;
		// o:
		case 'o':
			p++;
			switch(input[p]) {
			case 'n':
				if (apiLevel == AST.D0) {
					p++;
					if (input[p] == '_') {
						p++;
						if (input[p] == 's') {
							p++;
							if (input[p] == 'c') {
								p++;
								if (input[p] == 'o') {
									p++;
									if (input[p] == 'p') {
										p++;
										if (input[p] == 'e') {
											p++;
											if (input[p] == '_') {
												p++;
												switch(input[p]) {
												case 'e':
													p++;
													if (input[p] == 'x') {
														p++;
														if (input[p] == 'i') {
															p++;
															if (input[p] == 't' && !Chars.isidchar(input[p+1])) {
																t.value = TOK.TOKon_scope_exit;
																t.len = 13;
																p++;
																return;
															}
														}
													}
													break;
												case 'f':
													p++;
													if (input[p] == 'a') {
														p++;
														if (input[p] == 'i') {
															p++;
															if (input[p] == 'l') {
																p++;
																if (input[p] == 'u') {
																	p++;
																	if (input[p] == 'r') {
																		p++;
																		if (input[p] == 'e' && !Chars.isidchar(input[p+1])) {
																			t.value = TOK.TOKon_scope_failure;
																			t.len = 16;
																			p++;
																			return;
																		}
																	}
																}
															}
														}
													}
													break;
												case 's':
													p++;
													if (input[p] == 'u') {
														p++;
														if (input[p] == 'c') {
															p++;
															if (input[p] == 'c') {
																p++;
																if (input[p] == 'e') {
																	p++;
																	if (input[p] == 's') {
																		p++;
																		if (input[p] == 's' && !Chars.isidchar(input[p+1])) {
																			t.value = TOK.TOKon_scope_success;
																			t.len = 16;
																			p++;
																			return;
																		}
																	}
																}
															}
														}
													}
													break;
												}
											}
										}
									}
								}
							}
						}
					}
				}
				break;
			case 'u':
				p++;
				if (input[p] == 't' && !Chars.isidchar(input[p+1])) {
					t.value = TOK.TOKout;
					t.len = 3;
					p++;
					return;
				}
				break;
			case 'v':
				p++;
				if (input[p] == 'e') {
					p++;
					if (input[p] == 'r') {
						p++;
						if (input[p] == 'r') {
							p++;
							if (input[p] == 'i') {
								p++;
								if (input[p] == 'd') {
									p++;
									if (input[p] == 'e' && !Chars.isidchar(input[p+1])) {
										t.value = TOK.TOKoverride;
										t.len = 8;
										p++;
										return;
									}
								}
							}
						}
					}
				}
				break;
			}
			break;
		// p:
		case 'p':
			p++;
			switch(input[p]) {
			case 'a':
				p++;
				if (input[p] == 'c') {
					p++;
					if (input[p] == 'k') {
						p++;
						if (input[p] == 'a') {
							p++;
							if (input[p] == 'g') {
								p++;
								if (input[p] == 'e' && !Chars.isidchar(input[p+1])) {
									t.value = TOK.TOKpackage;
									t.len = 7;
									p++;
									return;
								}
							}
						}
					}
				}
				break;
			case 'r':
				p++;
				switch(input[p]) {
				case 'a':
					p++;
					if (input[p] == 'g') {
						p++;
						if (input[p] == 'm') {
							p++;
							if (input[p] == 'a' && !Chars.isidchar(input[p+1])) {
								t.value = TOK.TOKpragma;
								t.len = 6;
								p++;
								return;
							}
						}
					}
					break;
				case 'i':
					p++;
					if (input[p] == 'v') {
						p++;
						if (input[p] == 'a') {
							p++;
							if (input[p] == 't') {
								p++;
								if (input[p] == 'e' && !Chars.isidchar(input[p+1])) {
									t.value = TOK.TOKprivate;
									t.len = 7;
									p++;
									return;
								}
							}
						}
					}
					break;
				case 'o':
					p++;
					if (input[p] == 't') {
						p++;
						if (input[p] == 'e') {
							p++;
							if (input[p] == 'c') {
								p++;
								if (input[p] == 't') {
									p++;
									if (input[p] == 'e') {
										p++;
										if (input[p] == 'd' && !Chars.isidchar(input[p+1])) {
											t.value = TOK.TOKprotected;
											t.len = 9;
											p++;
											return;
										}
									}
								}
							}
						}
					}
					break;
				}
				break;
			case 'u':
				p++;
				if (input[p] == 'b') {
					p++;
					if (input[p] == 'l') {
						p++;
						if (input[p] == 'i') {
							p++;
							if (input[p] == 'c' && !Chars.isidchar(input[p+1])) {
								t.value = TOK.TOKpublic;
								t.len = 6;
								p++;
								return;
							}
						}
					}
				}
				break;
			}
			break;
		// r:
		case 'r':
			p++;
			if (input[p] == 'e') {
				p++;
				switch(input[p]) {
				case 'a':
					p++;
					if (input[p] == 'l' && !Chars.isidchar(input[p+1])) {
						t.value = TOK.TOKfloat80;
						t.len = 4;
						p++;
						return;
					}
					break;
				case 'f':
					if (!Chars.isidchar(input[p+1])) {
						t.value = TOK.TOKref;
						t.len = 3;
						p++;
						return;
					}
					break;
				case 't':
					p++;
					if (input[p] == 'u') {
						p++;
						if (input[p] == 'r') {
							p++;
							if (input[p] == 'n' && !Chars.isidchar(input[p+1])) {
								t.value = TOK.TOKreturn;
								t.len = 6;
								p++;
								return;
							}
						}
					}
					break;
				}
			}
			break;
		// s:
		case 's':
			p++;
			switch(input[p]) {
			case 'c':
				p++;
				if (input[p] == 'o') {
					p++;
					if (input[p] == 'p') {
						p++;
						if (input[p] == 'e' && !Chars.isidchar(input[p+1])) {
							t.value = TOK.TOKscope;
							t.len = 5;
							p++;
							return;
						}
					}
				}
				break;
			case 'h':
				p++;
				if (input[p] == 'o') {
					p++;
					if (input[p] == 'r') {
						p++;
						if (input[p] == 't' && !Chars.isidchar(input[p+1])) {
							t.value = TOK.TOKint16;
							t.len = 5;
							p++;
							return;
						}
					}
				}
				break;
			case 't':
				p++;
				switch(input[p]) {
				case 'a':
					p++;
					if (input[p] == 't') {
						p++;
						if (input[p] == 'i') {
							p++;
							if (input[p] == 'c' && !Chars.isidchar(input[p+1])) {
								t.value = TOK.TOKstatic;
								t.len = 6;
								p++;
								return;
							}
						}
					}
					break;
				case 'r':
					p++;
					if (input[p] == 'u') {
						p++;
						if (input[p] == 'c') {
							p++;
							if (input[p] == 't' && !Chars.isidchar(input[p+1])) {
								t.value = TOK.TOKstruct;
								t.len = 6;
								p++;
								return;
							}
						}
					}
					break;
				}
				break;
			case 'u':
				p++;
				if (input[p] == 'p') {
					p++;
					if (input[p] == 'e') {
						p++;
						if (input[p] == 'r' && !Chars.isidchar(input[p+1])) {
							t.value = TOK.TOKsuper;
							t.len = 5;
							p++;
							return;
						}
					}
				}
				break;
			case 'w':
				p++;
				if (input[p] == 'i') {
					p++;
					if (input[p] == 't') {
						p++;
						if (input[p] == 'c') {
							p++;
							if (input[p] == 'h' && !Chars.isidchar(input[p+1])) {
								t.value = TOK.TOKswitch;
								t.len = 6;
								p++;
								return;
							}
						}
					}
				}
				break;
			case 'y':
				p++;
				if (input[p] == 'n') {
					p++;
					if (input[p] == 'c') {
						p++;
						if (input[p] == 'h') {
							p++;
							if (input[p] == 'r') {
								p++;
								if (input[p] == 'o') {
									p++;
									if (input[p] == 'n') {
										p++;
										if (input[p] == 'i') {
											p++;
											if (input[p] == 'z') {
												p++;
												if (input[p] == 'e') {
													p++;
													if (input[p] == 'd' && !Chars.isidchar(input[p+1])) {
														t.value = TOK.TOKsynchronized;
														t.len = 12;
														p++;
														return;
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
				break;
			}
			break;
		// t:
		case 't':
			p++;
			switch(input[p]) {
			case 'e':
				p++;
				if (input[p] == 'm') {
					p++;
					if (input[p] == 'p') {
						p++;
						if (input[p] == 'l') {
							p++;
							if (input[p] == 'a') {
								p++;
								if (input[p] == 't') {
									p++;
									if (input[p] == 'e' && !Chars.isidchar(input[p+1])) {
										t.value = TOK.TOKtemplate;
										t.len = 8;
										p++;
										return;
									}
								}
							}
						}
					}
				}
				break;
			case 'h':
				p++;
				switch(input[p]) {
				case 'i':
					p++;
					if (input[p] == 's' && !Chars.isidchar(input[p+1])) {
						t.value = TOK.TOKthis;
						t.len = 4;
						p++;
						return;
					}
					break;
				case 'r':
					p++;
					if (input[p] == 'o') {
						p++;
						if (input[p] == 'w' && !Chars.isidchar(input[p+1])) {
							t.value = TOK.TOKthrow;
							t.len = 5;
							p++;
							return;
						}
					}
					break;
				}
				break;
			case 'r':
				p++;
				switch(input[p]) {
				case 'u':
					p++;
					if (input[p] == 'e' && !Chars.isidchar(input[p+1])) {
						t.value = TOK.TOKtrue;
						t.len = 4;
						p++;
						return;
					}
					break;
				case 'y':
					if (!Chars.isidchar(input[p+1])) {
						t.value = TOK.TOKtry;
						t.len = 3;
						p++;
						return;
					}
					break;
				}
				break;
			case 'y':
				p++;
				if (input[p] == 'p') {
					p++;
					if (input[p] == 'e') {
						p++;
						switch(input[p]) {
						case 'd':
							p++;
							if (input[p] == 'e') {
								p++;
								if (input[p] == 'f' && !Chars.isidchar(input[p+1])) {
									t.value = TOK.TOKtypedef;
									t.len = 7;
									p++;
									return;
								}
							}
							break;
						case 'i':
							p++;
							if (input[p] == 'd' && !Chars.isidchar(input[p+1])) {
								t.value = TOK.TOKtypeid;
								t.len = 6;
								p++;
								return;
							}
							break;
						case 'o':
							p++;
							if (input[p] == 'f' && !Chars.isidchar(input[p+1])) {
								t.value = TOK.TOKtypeof;
								t.len = 6;
								p++;
								return;
							}
							break;
						}
					}
				}
				break;
			}
			break;
		// u:
		case 'u':
			p++;
			switch(input[p]) {
			case 'b':
				p++;
				if (input[p] == 'y') {
					p++;
					if (input[p] == 't') {
						p++;
						if (input[p] == 'e' && !Chars.isidchar(input[p+1])) {
							t.value = TOK.TOKuns8;
							t.len = 5;
							p++;
							return;
						}
					}
				}
				break;
			case 'c':
				p++;
				if (input[p] == 'e') {
					p++;
					if (input[p] == 'n') {
						p++;
						if (input[p] == 't' && !Chars.isidchar(input[p+1])) {
							t.value = TOK.TOKucent;
							t.len = 5;
							p++;
							return;
						}
					}
				}
				break;
			case 'i':
				p++;
				if (input[p] == 'n') {
					p++;
					if (input[p] == 't' && !Chars.isidchar(input[p+1])) {
						t.value = TOK.TOKuns32;
						t.len = 4;
						p++;
						return;
					}
				}
				break;
			case 'l':
				p++;
				if (input[p] == 'o') {
					p++;
					if (input[p] == 'n') {
						p++;
						if (input[p] == 'g' && !Chars.isidchar(input[p+1])) {
							t.value = TOK.TOKuns64;
							t.len = 5;
							p++;
							return;
						}
					}
				}
				break;
			case 'n':
				p++;
				if (input[p] == 'i') {
					p++;
					switch(input[p]) {
					case 'o':
						p++;
						if (input[p] == 'n' && !Chars.isidchar(input[p+1])) {
							t.value = TOK.TOKunion;
							t.len = 5;
							p++;
							return;
						}
						break;
					case 't':
						p++;
						if (input[p] == 't') {
							p++;
							if (input[p] == 'e') {
								p++;
								if (input[p] == 's') {
									p++;
									if (input[p] == 't' && !Chars.isidchar(input[p+1])) {
										t.value = TOK.TOKunittest;
										t.len = 8;
										p++;
										return;
									}
								}
							}
						}
						break;
					}
				}
				break;
			case 's':
				p++;
				if (input[p] == 'h') {
					p++;
					if (input[p] == 'o') {
						p++;
						if (input[p] == 'r') {
							p++;
							if (input[p] == 't' && !Chars.isidchar(input[p+1])) {
								t.value = TOK.TOKuns16;
								t.len = 6;
								p++;
								return;
							}
						}
					}
				}
				break;
			}
			break;
		// v:
		case 'v':
			p++;
			switch(input[p]) {
			case 'e':
				p++;
				if (input[p] == 'r') {
					p++;
					if (input[p] == 's') {
						p++;
						if (input[p] == 'i') {
							p++;
							if (input[p] == 'o') {
								p++;
								if (input[p] == 'n' && !Chars.isidchar(input[p+1])) {
									t.value = TOK.TOKversion;
									t.len = 7;
									p++;
									return;
								}
							}
						}
					}
				}
				break;
			case 'o':
				p++;
				switch(input[p]) {
				case 'i':
					p++;
					if (input[p] == 'd' && !Chars.isidchar(input[p+1])) {
						t.value = TOK.TOKvoid;
						t.len = 4;
						p++;
						return;
					}
					break;
				case 'l':
					p++;
					if (input[p] == 'a') {
						p++;
						if (input[p] == 't') {
							p++;
							if (input[p] == 'i') {
								p++;
								if (input[p] == 'l') {
									p++;
									if (input[p] == 'e' && !Chars.isidchar(input[p+1])) {
										t.value = TOK.TOKvolatile;
										t.len = 8;
										p++;
										return;
									}
								}
							}
						}
					}
					break;
				}
				break;
			}
			break;
		// w:
		case 'w':
			p++;
			switch(input[p]) {
			case 'c':
				p++;
				if (input[p] == 'h') {
					p++;
					if (input[p] == 'a') {
						p++;
						if (input[p] == 'r' && !Chars.isidchar(input[p+1])) {
							t.value = TOK.TOKwchar;
							t.len = 5;
							p++;
							return;
						}
					}
				}
				break;
			case 'h':
				p++;
				if (input[p] == 'i') {
					p++;
					if (input[p] == 'l') {
						p++;
						if (input[p] == 'e' && !Chars.isidchar(input[p+1])) {
							t.value = TOK.TOKwhile;
							t.len = 5;
							p++;
							return;
						}
					}
				}
				break;
			case 'i':
				p++;
				if (input[p] == 't') {
					p++;
					if (input[p] == 'h' && !Chars.isidchar(input[p+1])) {
						t.value = TOK.TOKwith;
						t.len = 4;
						p++;
						return;
					}
				}
				break;
			}
			break;
		// _
		case '_':
			p++;
			if (input[p] == '_') {
				p++;
				switch(input[p]) {
				case 'D':
					p++;
					if (input[p] == 'A') {
						p++;
						if (input[p] == 'T') {
							p++;
							if (input[p] == 'E') {
								p++;
								if (input[p] == '_') {
									p++;
									if (input[p] == '_' && !Chars.isidchar(input[p+1])) {
										t.value = TOK.TOKstring;
										t.special = Token.SPECIAL__DATE__;
										t.string = "__DATE__";
										t.len = 8;
										p++;
										return;
									}
								}
							}
						}
					}
					break;
				case 'F':
					p++;
					if (input[p] == 'I') {
						p++;
						if (input[p] == 'L') {
							p++;
							if (input[p] == 'E') {
								p++;
								if (input[p] == '_') {
									p++;
									if (input[p] == '_' && !Chars.isidchar(input[p+1])) {
										t.value = TOK.TOKstring;
										t.special = Token.SPECIAL__FILE__;
										t.string = "__FILE__";
										t.len = 8;
										p++;
										return;
									}
								}
							}
						}
					}
					break;
				case 'L':
					p++;
					if (input[p] == 'I') {
						p++;
						if (input[p] == 'N') {
							p++;
							if (input[p] == 'E') {
								p++;
								if (input[p] == '_') {
									p++;
									if (input[p] == '_' && !Chars.isidchar(input[p+1])) {
										t.value = TOK.TOKint64v;
										t.special = Token.SPECIAL__LINE__;
										t.string = "__LINE__";
										t.len = 8;
										p++;
										return;
									}
								}
							}
						}
					}
					break;
				case 'T':
					p++;
					if (input[p] == 'I') {
						p++;
						if (input[p] == 'M') {
							p++;
							if (input[p] == 'E') {
								p++;
								switch(input[p]) {
								case 'S':
									p++;
									if (input[p] == 'T') {
										p++;
										if (input[p] == 'A') {
											p++;
											if (input[p] == 'M') {
												p++;
												if (input[p] == 'P') {
													p++;
													if (input[p] == '_') {
														p++;
														if (input[p] == '_' && !Chars.isidchar(input[p+1])) {
															t.value = TOK.TOKstring;
															t.special = Token.SPECIAL__TIMESTAMP__;
															t.string = "__TIMESTAMP__";
															t.len = 13;
															p++;
															return;
														}
													}
												}
											}
										}
									}
									break;
								case '_':
									p++;
									if (input[p] == '_' && !Chars.isidchar(input[p+1])) {
										t.value = TOK.TOKstring;
										t.special = Token.SPECIAL__TIME__;
										t.string = "__TIME__";
										t.len = 8;
										p++;
										return;
									}
									break;
								}
							}
						}
					}
					break;
				case 'V':
					p++;
					if (input[p] == 'E') {
						p++;
						switch(input[p]) {
						case 'N':
							p++;
							if (input[p] == 'D') {
								p++;
								if (input[p] == 'O') {
									p++;
									if (input[p] == 'R') {
										p++;
										if (input[p] == '_') {
											p++;
											if (input[p] == '_' && !Chars.isidchar(input[p+1])) {
												t.value = TOK.TOKstring;
												t.special = Token.SPECIAL__VENDOR__;
												t.string = "__VENDOR__";
												t.len = 10;
												p++;
												return;
											}
										}
									}
								}
							}
							break;
						case 'R':
							p++;
							if (input[p] == 'S') {
								p++;
								if (input[p] == 'I') {
									p++;
									if (input[p] == 'O') {
										p++;
										if (input[p] == 'N') {
											p++;
											if (input[p] == '_') {
												p++;
												if (input[p] == '_' && !Chars.isidchar(input[p+1])) {
													t.value = TOK.TOKint64v;
													t.special = Token.SPECIAL__VERSION__;
													t.string = "__VERSION__";
													t.len = 11;
													p++;
													return;
												}
											}
										}
									}
								}
							}
							break;
						}
					}
					break;
				case 't':
					if (apiLevel == AST.D2) {
						p++;
						if (input[p] == 'r') {
							p++;
							if (input[p] == 'a') {
								p++;
								if (input[p] == 'i') {
									p++;
									if (input[p] == 't') {
										p++;
										if (input[p] == 's' && !Chars.isidchar(input[p+1])) {
											t.value = TOK.TOKtraits;
											t.len = 8;
											p++;
											return;
										}
									}
								}
							}
						}
						break;
					}
				}
			}
			break;
		}
		
		case_ident_other(t);
	}
	
	private void case_ident_other(Token t) {
		char c = input[p];

		if (c > 0 && Chars.isidchar(c) || (c >= 0x80 && UniAlpha.isUniAlpha(decodeUTF()))) {
			do
			{
				c = input[++p];
			} while (c > 0 && Chars.isidchar(c) || (c >= 0x80 && UniAlpha.isUniAlpha(decodeUTF())));
		}
		
		t.value = TOK.TOKidentifier;
		t.len = p - t.ptr;
		t.string = new String(input, t.ptr, t.len);
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
	
	protected void newline(boolean inComment) {
		if (recordLineSeparator && linnum == maxLinnum) {
			final int INCREMENT = 250;
			int length = this.lineEnds.length;
			if (this.linnum - 1 >=  length)
				System.arraycopy(this.lineEnds, 0, this.lineEnds = new int[length + INCREMENT], 0, length);
			this.lineEnds[this.linnum - 1] = p;
		}
		this.linnum++;
		if (this.linnum > maxLinnum) {
			maxLinnum = this.linnum;
		}
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
	
	/**
	 * Search the source position corresponding to the beginning of a given line number
	 *
	 * Line numbers are 1-based, and relative to the scanner initialPosition. 
	 * Character positions are 0-based.
	 *
	 * e.g.	getLineStart(1) --> 0	indicates that the first line starts at character 0.
	 *
	 * In case the given line number is inconsistent, answers -1.
	 * 
	 * @param lineNumber int
	 * @return int
	 */
	public final int getLineStart(int lineNumber) {

		if (this.lineEnds == null || this.linnum == -1) 
			return -1;
		if (lineNumber > this.lineEnds.length + 1) 
			return -1;
		if (lineNumber <= 0) 
			return -1;
		
		if (lineNumber == 1) 
			return this.base;
		return this.lineEnds[lineNumber-2]+1; // next line start one character behind the lineEnd of the previous line
	}
	
	/*
	 * Search the source position corresponding to the end of a given line number
	 *
	 * Line numbers are 1-based, and relative to the scanner initialPosition. 
	 * Character positions are 0-based.
	 *
	 * In case the given line number is inconsistent, answers -1.
	 */
	public final int getLineEnd(int lineNumber) {

		if (this.lineEnds == null || this.linnum == -1) 
			return -1;
		if (lineNumber > this.lineEnds.length+1) 
			return -1;
		if (lineNumber <= 0) 
			return -1;
		if (lineNumber == this.lineEnds.length + 1 || lineNumber == this.linnum) 
			return this.end;
		return this.lineEnds[lineNumber-1]; // next line start one character behind the lineEnd of the previous line
	}
	
	/**
	 * Search the line number corresponding to a specific position
	 * @param position int
	 * @return int
	 */
	public final int getLineNumber(int position) {

		if (this.lineEnds == null)
			return 1;
		int length = this.maxLinnum-1;
		if (length == 0)
			return 1;
		int g = 0, d = length - 1;
		int m = 0;
		while (g <= d) {
			m = (g + d) /2;
			if (position < this.lineEnds[m]) {
				d = m-1;
			} else if (position > this.lineEnds[m]) {
				g = m+1;
			} else {
				return m + 1;
			}
		}
		if (position < this.lineEnds[m]) {
			return m+1;
		}
		return m+2;
	}
	
	public int[] getLineEnds() {
		// return a bounded copy of this.lineEnds
		if (this.linnum == -1) {
			return EMPTY_LINE_ENDS;
		}
		int[] copy;
		System.arraycopy(this.lineEnds, 0, copy = new int[this.linnum - 1], 0, this.linnum - 1);
		return copy;
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
	
	// task tags
	
	// check presence of task: tags
	// TODO (frederic, from JDT) see if we need to take unicode characters into account...
	public void checkTaskTag(int commentStart, int commentEnd) {
		char[] src = this.input;
		
		// only look for newer task: tags
		if (this.foundTaskCount > 0
			&& this.foundTaskPositions[this.foundTaskCount - 1][0] >= commentStart) {
			return;
		}
		int foundTaskIndex = this.foundTaskCount;
		char previous = src[commentStart+1]; // should be '*' or '/'
		for (
			int i = commentStart + 2; i < commentEnd && i < this.end; i++) {
			char[] tag = null;
			char[] priority = null;
			// check for tag occurrence only if not ambiguous with javadoc tag
			// TODO remove this check for D
			if (previous != '@') {
				nextTag : for (int itag = 0; itag < this.taskTags.length; itag++) {
					tag = this.taskTags[itag];
					int tagLength = tag.length;
					if (tagLength == 0) continue nextTag;
		
					// ensure tag is not leaded with letter if tag starts with a letter
					if (ScannerHelper.isJavaIdentifierStart(tag[0])) {
						if (ScannerHelper.isJavaIdentifierPart(previous)) {
							continue nextTag;
						}
					}
		
					for (int t = 0; t < tagLength; t++) {
						char sc, tc;
						int x = i+t;
						if (x >= this.end || x >= commentEnd) continue nextTag;
						if ((sc = src[i + t]) != (tc = tag[t])) { 																					// case sensitive check
							if (this.isTaskCaseSensitive || (ScannerHelper.toLowerCase(sc) != ScannerHelper.toLowerCase(tc))) { 	// case insensitive check
								continue nextTag;
							}
						}
					}
					// ensure tag is not followed with letter if tag finishes with a letter
					if (i+tagLength < commentEnd && ScannerHelper.isJavaIdentifierPart(src[i+tagLength-1])) {
						if (ScannerHelper.isJavaIdentifierPart(src[i + tagLength]))
							continue nextTag;
					}
					if (this.foundTaskTags == null) {
						this.foundTaskTags = new char[5][];
						this.foundTaskMessages = new char[5][];
						this.foundTaskPriorities = new char[5][];
						this.foundTaskPositions = new int[5][];
					} else if (this.foundTaskCount == this.foundTaskTags.length) {
						System.arraycopy(this.foundTaskTags, 0, this.foundTaskTags = new char[this.foundTaskCount * 2][], 0, this.foundTaskCount);
						System.arraycopy(this.foundTaskMessages, 0, this.foundTaskMessages = new char[this.foundTaskCount * 2][], 0, this.foundTaskCount);
						System.arraycopy(this.foundTaskPriorities, 0, this.foundTaskPriorities = new char[this.foundTaskCount * 2][], 0, this.foundTaskCount);
						System.arraycopy(this.foundTaskPositions, 0, this.foundTaskPositions = new int[this.foundTaskCount * 2][], 0, this.foundTaskCount);
					}
					
					priority = this.taskPriorities != null && itag < this.taskPriorities.length
								? this.taskPriorities[itag]
								: null;
					
					this.foundTaskTags[this.foundTaskCount] = tag;
					this.foundTaskPriorities[this.foundTaskCount] = priority;
					this.foundTaskPositions[this.foundTaskCount] = new int[] { i, i + tagLength - 1 };
					this.foundTaskMessages[this.foundTaskCount] = CharOperation.NO_CHAR;
					this.foundTaskCount++;
					i += tagLength - 1; // will be incremented when looping
					break nextTag;
				}
			}
			previous = src[i];
		}
		boolean containsEmptyTask = false;
		for (int i = foundTaskIndex; i < this.foundTaskCount; i++) {
			// retrieve message start and end positions
			int msgStart = this.foundTaskPositions[i][0] + this.foundTaskTags[i].length;
			int max_value = i + 1 < this.foundTaskCount
					? this.foundTaskPositions[i + 1][0] - 1
					: commentEnd - 1;
			// at most beginning of next task
			if (max_value < msgStart) {
				max_value = msgStart; // would only occur if tag is before EOF.
			}
			int end = -1;
			char c;
			for (int j = msgStart; j < max_value; j++) {
				if ((c = src[j]) == '\n' || c == '\r') {
					end = j - 1;
					break;
				}
			}
			if (end == -1) {
				for (int j = max_value; j > msgStart; j--) {
					if ((c = src[j]) == '*') {
						end = j - 1;
						break;
					}
				}
				if (end == -1)
					end = max_value;
			}
			if (msgStart == end) {
				// if the description is empty, we might want to see if two tags are not sharing the same message
				// see https://bugs.eclipse.org/bugs/show_bug.cgi?id=110797
				containsEmptyTask = true;
				continue;
			}
			// trim the message
			while (CharOperation.isWhitespace(src[end]) && msgStart <= end)
				end--;
			while (CharOperation.isWhitespace(src[msgStart]) && msgStart <= end)
				msgStart++;
			// update the end position of the task
			this.foundTaskPositions[i][1] = end;
			// get the message source
			final int messageLength = end - msgStart + 1;
			char[] message = new char[messageLength];
			System.arraycopy(src, msgStart, message, 0, messageLength);
			this.foundTaskMessages[i] = message;
		}
		if (containsEmptyTask) {
			for (int i = foundTaskIndex, max = this.foundTaskCount; i < max; i++) {
				if (this.foundTaskMessages[i].length == 0) {
					loop: for (int j = i + 1; j < max; j++) {
						if (this.foundTaskMessages[j].length != 0) {
							this.foundTaskMessages[i] = this.foundTaskMessages[j];
							this.foundTaskPositions[i][1] = this.foundTaskPositions[j][1];
							break loop;
						}
					}
				}
			}
		}
	}

}
