package descent.internal.core.dom;

import static descent.internal.core.dom.TOK.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import descent.core.compiler.IProblem;
import descent.core.dom.IProblemCollector;

public class Lexer implements IProblemCollector {
	
	public final static BigInteger X_FFFFFFFFFFFFFFFF =     new BigInteger("FFFFFFFFFFFFFFFF", 16);
	public final static BigInteger X_8000000000000000 =         new BigInteger("8000000000000000", 16);
	public final static BigInteger X_FFFFFFFF00000000  =       new BigInteger("FFFFFFFF00000000", 16);
	public final static BigInteger X_80000000 =                 new BigInteger("80000000", 16);
	public final static BigInteger X_FFFFFFFF80000000 =     new BigInteger("FFFFFFFF80000000", 16);
	                                                                       
	public final static int LS = 0x2028;
	public final static int PS = 0x2029;
	
	public StringTable stringtable = new StringTable();
	public OutBuffer stringbuffer = new OutBuffer();
	public Token freelist;
	
	public int linnum;
	
	public Module mod = new Module();
	//public int base;
	public int p;
	public int end;
	public char[] input;
	public Token token;
	public Token prevToken;
	public boolean doDocComment;
	public boolean anyToken;
	public boolean commentToken;
	
	public List<IProblem> problems;
	public List<Token> tokenList;
    
    public Lexer(String source) {
    	this(source, 0, source.length(), true, false);
    }
	
    /* XXX: Mmrnmhrm: A fix from DMD was made here */
	public Lexer(String source, int begoffset, 
			int endoffset, boolean doDocComment, boolean commentToken) {
		
		initKeywords();
		
		this.problems = new ArrayList<IProblem>();
		this.tokenList = new ArrayList<Token>();
		
		// Make input larger and add zeros, to avoid comparing
		// TODO: make input of the range size only
		input = new char[endoffset + 5];
		source.getChars(0, endoffset, input, 0);
		this.linnum = 1;
		//this.base = base;
		//this.end = base + endoffset;
		//this.p = base + begoffset;
		this.end = endoffset ;
		this.p = begoffset;
		this.doDocComment = doDocComment;
	    this.anyToken = false;
	    this.commentToken = commentToken;
	    this.token = new Token();
	    
	    if (input[p] == '#' && input[p+1] =='!')
	    {
	    	p += 2;
	    	while (true)
	    	{
	    		char c = input[p];
			    switch (c)
			    {
					case '\n':
					    p++;
					    break;
	
				case '\r':
				    p++;
				    if (input[p] == '\n')
					p++;
				    break;
	
				case 0:
				case 0x1A:
				    break;
	
				default:
				    if ((c & 0x80) != 0)
				    {   int u = decodeUTF();
					if (u == PS || u == LS)
					    break;
				    }
				    p++;
				    continue;
			    }
			    break;
			}
	    	linnum = 2;
	    }
	}
	
	public void problem(String message, int severity, int id, int offset, int length) {
		problems.add(new Problem(message, severity, id, offset, length));
	}
	
	public void collectProblem(IProblem problem) {
		problems.add(problem);
	}	
	
	public TOK nextToken() {
		Token t;
		
		if (token != null) {
			prevToken = new Token(token);
			tokenList.add(prevToken);
		}

	    if (token.next != null) {
	    	t = token.next;
	    	token = new Token(t);
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
	    	t = new Token();
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
	
	/**********************************
	 * Determine if string is a valid Identifier.
	 * Placed here because of commonality with Lexer functionality.
	 */

	/*
	TODO: not used yet
	public boolean isValidIdentifier(String p)
	{
	    int len;
		int[] idx = { 0 };
		char[] chars;

		if (p == null || p.length() == 0)
			return false;

		if (Chars.isdigit(p.charAt(0)))
			return false;

		len = p.length();
		idx[0] = 0;
		chars = p.toCharArray();
		while (p.charAt(idx[0]) != 0) {
			int[] dc = { 0 };

			String q = Utf.decodeChar(chars, 0, len, idx, dc);
			if (q != null)
				return false;

			if (!((dc[0] >= 0x80 && UniAlpha.isUniAlpha(dc[0]))
					|| Chars.isalnum(dc[0]) || dc[0] == '_'))
				return false;
		}
		return true;
	}
	*/
	
	public void scan(Token t) {
		int lastLine = this.linnum;
	    int linnum = this.linnum;
	    
	    t.blockComment = null;
	    t.lineComment = null;
	    while (true)
	    {
		t.ptr = p;
		//printf("p = %p, *p = '%c'\n",p,*p);
		switch (input[p])
		{
		    case 0:
		    case 0x1A:
			t.value = TOKeof;			// end of file
			t.len = 0;
			return;

		    case ' ':
		    case '\t':
		    // TODO: case '\v':
		    case '\f':
			p++;
			continue;			// skip white space

		    case '\r':
			p++;
			if (input[p] != '\n')			// if CR stands by itself
			    linnum++;
			continue;			// skip white space

		    case '\n':
			p++;
			linnum++;
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
			t.value = wysiwygStringConstant(t, input[p]);
			t.len = p - t.ptr;
			return;

		    case 'x':
			if (input[p + 1] != '"') {
				case_ident(t);
		    	return;
			}
			p++;
			t.value = hexStringConstant(t);
			t.len = p - t.ptr;
			return;


		    case '"':
			t.value = escapeStringConstant(t,0);
			t.len = p - t.ptr;
			return;

		    case '\\':			// escaped string literal
		    {	int c;

				stringbuffer.reset();
			do
			{
			    p++;
			    c = escapeSequence();
			    stringbuffer.writeUTF8(c);
			} while (input[p] == '\\');
				t.len = p - t.ptr;
				t.ustring = stringbuffer.data.toString();
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
						linnum++;
						p++;
						continue;

					    case '\r':
						p++;
						if (input[p] != '\n')
						    linnum++;
						continue;

					    case 0:
					    case 0x1A:
					    	problem("Unterminated block comment", IProblem.SEVERITY_ERROR, IProblem.UNTERMINATED_BLOCK_COMMENT, t.ptr, p - t.ptr);
					    	p = end;
					    	t.value = TOKeof;
					    	return;

					    default:
						if ((c & 0x80) != 0)
						{   int u = decodeUTF();
						    if (u == PS || u == LS)
							linnum++;
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
				if (commentToken)
				{
				    t.value = TOKcomment;
				    t.len = p - t.ptr;
				    return;
				}
				else if (doDocComment && input[t.ptr + 2] == '*' && p - 4 != t.ptr)
				{   // if /** but not /**/
				    getDocComment(t, lastLine == linnum);
				}
				continue;

			    case '/':		// do // style comments
				linnum = this.linnum;
				while (true)
				{   char c = input[++p];
				    switch (c)
				    {
					case '\n':
					    break;

					case '\r':
					    if (input[p + 1] == '\n')
						p++;
					    break;

					case 0:
					case 0x1A:
					    if (commentToken)
					    {
						p = end;
						t.value = TOKcomment;
					    t.len = p - t.ptr;
						return;
					    }
					    if (doDocComment && input[t.ptr + 2] == '/')
						getDocComment(t, lastLine == linnum);
					    p = end;
					    t.value = TOKeof;
					    return;

					default:
					    if ((c & 0x80) != 0)
					    {   int u = decodeUTF();
						if (u == PS || u == LS)
						    break;
					    }
					    continue;
				    }
				    break;
				}

				if (commentToken)
				{
				    p++;
				    linnum++;
				    t.value = TOKcomment;
				    t.len = p - t.ptr;
				    return;
				}
				if (doDocComment && input[t.ptr + 2] == '/')
				    getDocComment(t, lastLine == linnum);

				p++;
				linnum++;
				continue;

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
					    if (input[p] != '\n')
						linnum++;
					    continue;

					case '\n':
					    linnum++;
					    p++;
					    continue;

					case 0:
					case 0x1A:
						problem("Unterminated plus block comment", IProblem.SEVERITY_ERROR, IProblem.UNTERMINATED_PLUS_BLOCK_COMMENT, t.ptr, p - t.ptr);
					    p = end;
					    t.value = TOKeof;
					    return;

					default:
					    if ((c & 0x80) != 0)
					    {   int u = decodeUTF();
						if (u == PS || u == LS)
						    linnum++;
					    }
					    p++;
					    continue;
				    }
				    break;
				}
				if (commentToken)
				{
				    t.value = TOKcomment;
				    t.len = p - t.ptr;
				    return;
				}
				if (doDocComment && input[t.ptr + 2] == '+' && p - 4 != t.ptr)
				{   // if /++ but not /++/
				    getDocComment(t, lastLine == linnum);
				}
				continue;
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
			    if (input[p] == '=')
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
			    if (input[p] == '=')
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
			pragma();
			continue;

		    default:
		    {	char c = input[p];

			if ((c & 0x80) != 0)
			{   
				int u = decodeUTF();

			    // Check for start of unicode identifier
			    if (UniAlpha.isUniAlpha(u)) {
			    	case_ident(t);
			    	return;
			    }

			    if (u == PS || u == LS)
			    {
				linnum++;
				p++;
				continue;
			    }
			}
			if (Chars.isprint(c)) {
				problem("Unsupported char: " + (char) c, IProblem.SEVERITY_ERROR, IProblem.UNSUPPORTED_CHAR, p, 1);
			} else {
				problem("Unsupported char: 0x" + Integer.toHexString(c), IProblem.SEVERITY_ERROR, IProblem.UNSUPPORTED_CHAR, p, 1);
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
		} while (c > 0 && Chars.isidchar(c) || ((c & 0x80) != 0 && UniAlpha.isUniAlpha(decodeUTF())));
		sv = stringtable.update(input, t.ptr, p - t.ptr);
		id = (Identifier) sv.ptrvalue;
		if (id == null)
		{   id = new Identifier(sv.lstring,TOKidentifier);
		    sv.ptrvalue = id;
		}
		t.ident = id;
		t.value = id.value;
		t.len = id.string.length();
		
		anyToken = true;
		if (input[t.ptr] == '_') // if special identifier token
		{
			// TODO
			/*
		    static char date[11+1];
		    static char time[8+1];
		    static char timestamp[24+1];

		    if (!date[0])	// lazy evaluation
		    {   time_t t;
			char *p;

			::time(&t);
			p = ctime(&t);
			assert(p);
			sprintf(date, "%.6s %.4s", p + 4, p + 20);
			sprintf(time, "%.8s", p + 11);
			sprintf(timestamp, "%.24s", p);
		    }

		    if (mod && id == Id::FILE)
		    {
			t->value = TOKstring;
			t->ustring = (unsigned char *)(loc.filename ? loc.filename : mod->ident->toChars());
			goto Llen;
		    }
		    else if (mod && id == Id::LINE)
		    {
			t->value = TOKint64v;
			t->uns64value = linnum;
		    }
		    else if (id == Id::DATE)
		    {
			t->value = TOKstring;
			t->ustring = (unsigned char *)date;
			goto Llen;
		    }
		    else if (id == Id::TIME)
		    {
			t->value = TOKstring;
			t->ustring = (unsigned char *)time;
			goto Llen;
		    }
		    else if (id == Id::TIMESTAMP)
		    {
			t->value = TOKstring;
			t->ustring = (unsigned char *)timestamp;
		     Llen:
			t->postfix = 0;
			t->len = strlen((char *)t->ustring);
		    }
		    */
		}
		//printf("t->value = %d\n",t->value);
		return;
	}
	
	private int escapeSequence() {
		int c;
	    int n;
	    int ndigits = 0;
	    int startOfNumber;

	    c = input[p];
	    switch (c)
	    {
		case '\'':
		case '"':
		case '?':
		case '\\':
			p++;
			break;

		case 'a':	c = 7; p++; break;
		case 'b':	c = 8; p++; break;
		case 'f':	c = 12; p++; break;
		case 'n':	c = 10; p++; break;
		case 'r':	c = 13; p++; break;
		case 't':	c = 9; p++; break;
		case 'v':	c = 11; p++; break;

		case 'u':
			ndigits = 4;
		case 'U':
			if (ndigits == 0)
				ndigits = 8;
		case 'x':
			startOfNumber = p - 1;
			if (ndigits == 0)
				ndigits = 2;
			p++;
			c = input[p];
			if (Chars.ishex(c)) {
				long v;

				n = 0;
				v = 0;
				while (true) {
					if (Chars.isdigit(c))
						c -= '0';
					else if (Chars.islower(c))
						c -= 'a' - 10;
					else
						c -= 'A' - 10;
					v = v * 16 + c;
					c = input[++p];
					if (++n == ndigits)
						break;
					if (!Chars.ishex(c)) {
						problem(
								"Escape hex sequence has " + n
										+ " digits instead of " + ndigits,
								IProblem.SEVERITY_ERROR,
								IProblem.INCORRECT_NUMBER_OF_HEX_DIGITS_IN_ESCAPE_SEQUENCE,
								startOfNumber, p - startOfNumber);
						break;
					}
				}
				if (ndigits != 2 && !Utf.isValidDchar(v)) {
					problem(
							"Invalid UTF character: \\U" + Long.toHexString(v),
							IProblem.SEVERITY_ERROR,
							IProblem.INVALID_UTF_CHARACTER, startOfNumber, p - startOfNumber);
				}
				c = (int) v;
			} else {
				problem("Undefined escape hex sequence",
						IProblem.SEVERITY_ERROR,
						IProblem.UNDEFINED_ESCAPE_HEX_SEQUENCE, startOfNumber,
						p - startOfNumber + 1);
			}
			break;

		case '&':			// named character entity
			for (int idstart = ++p; true; p++)
			{
			    switch (input[p])
			    {
				case ';':
				    c = Entity.HtmlNamedEntity(input, idstart, p - idstart, this);
				    if (c == ~0)
				    {   
				    	// TODO: ?
				    	//error("unnamed character entity &%.*s;", p - idstart, idstart);
				    	c = ' ';
				    }
				    p++;
				    break;

				default:
				    if (Chars.isalpha(input[p]) ||
					(p != idstart + 1 && Chars.isdigit(input[p])))
					continue;
				    problem("Unterminated named entity", IProblem.SEVERITY_ERROR,
				    		IProblem.UNTERMINATED_NAMED_ENTITY, idstart - 1, p - idstart + 1);
				    break;
			    }
			    break;
			}
			break;

		case 0:
		case 0x1A:			// end of file
			c = '\\';
			break;

		default:
			if (Chars.isoctal(c))
			{   int v;

			    n = 0;
			    v = 0;
			    do
			    {
				v = v * 8 + (c - '0');
				c = input[++p];
			    } while (++n < 3 && Chars.isoctal(c));
			    c = v;
			}
			else {
				problem("Undefined escape sequence", IProblem.SEVERITY_ERROR, IProblem.UNDEFINED_ESCAPE_SEQUENCE, p - 1, 2);
			}
			break;
	    }
	    return c;
	}
	
	private TOK wysiwygStringConstant(Token t, int tc) {
		int c;

	    p++;
	    stringbuffer.reset();
	    while (true)
	    {
		c = input[p++];
		switch (c)
		{
		    case '\n':
			linnum++;
			break;

		    case '\r':
			if (input[p] == '\n')
			    continue;	// ignore
			c = '\n';	// treat EndOfLine as \n character
			linnum++;
			break;

		    case 0:
		    case 0x1A: {
		    	problem("Unterminated string constant", IProblem.SEVERITY_ERROR, IProblem.UNTERMINATED_STRING_CONSTANT, token.ptr, p - token.ptr - 1);
				t.ustring = "";
				t.len = 0;
				t.postfix = 0;
				return TOKstring;
		    }

		    case '"':
		    case '`':
			if (c == tc)
			{
			    t.len = stringbuffer.data.length();
			    t.ustring = stringbuffer.data.toString();
			    stringPostfix(t);
			    return TOKstring;
			}
			break;

		    default:
			if ((c & 0x80) != 0)
			{   p--;
			    int u = decodeUTF();
			    p++;
			    if (u == PS || u == LS)
				linnum++;
			    stringbuffer.writeUTF8(u);
			    continue;
			}
			break;
		}
		stringbuffer.writeByte(c);
	    }
	}
	
	private TOK hexStringConstant(Token t) {
		int c;
	    int n = 0;
	    int v = 0;

	    p++;
	    stringbuffer.reset();
	    while (true)
	    {
		c = input[p++];
		switch (c)
		{
		    case ' ':
		    case '\t':
		    // XXX: case '\v':
		    case '\f':
			continue;			// skip white space

		    case '\r':
			if (input[p] == '\n')
			    continue;			// ignore
			// Treat isolated '\r' as if it were a '\n'
		    case '\n':
			linnum++;
			continue;

		    case 0:
		    case 0x1A: {
		    	problem("Unterminated string constant", IProblem.SEVERITY_ERROR, IProblem.UNTERMINATED_STRING_CONSTANT, token.ptr, p - token.ptr - 1);
				t.ustring = "";
				t.len = 0;
				t.postfix = 0;
				return TOKstring;
		    }

		    case '"':
			if ((n & 1) != 0)
			{   
				problem("Odd number (" + n + ") of hex characters in hex string", IProblem.SEVERITY_ERROR, IProblem.ODD_NUMBER_OF_CHARACTERS_IN_HEX_STRING, token.ptr, p - token.ptr);
			    stringbuffer.writeByte(v);
			}
			t.len = stringbuffer.data.length();
			t.ustring = stringbuffer.data.toString();
			stringPostfix(t);
			return TOKstring;

		    default:
			if (c >= '0' && c <= '9')
			    c -= '0';
			else if (c >= 'a' && c <= 'f')
			    c -= 'a' - 10;
			else if (c >= 'A' && c <= 'F')
			    c -= 'A' - 10;
			else if ((c & 0x80) != 0)
			{   p--;
			    int u = decodeUTF();
			    p++;
			    if (u == PS || u == LS)
				linnum++;
			    else {
			    	problem("Non-hex character: " + (char) u, IProblem.SEVERITY_ERROR, IProblem.NON_HEX_CHARACTER, p - 1, 1);
			    }
			}
			else {
			    problem("Non-hex character: " + (char) c, IProblem.SEVERITY_ERROR, IProblem.NON_HEX_CHARACTER, p - 1, 1);
			}
			if ((n & 1) != 0)
			{   v = (v << 4) | c;
			    stringbuffer.writeByte(v);
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

	    p++;
	    stringbuffer.reset();
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
				c = escapeSequence();
				stringbuffer.writeUTF8(c);
				continue;

			    default:
				c = escapeSequence();
				break;
			}
			break;

		    case '\n':
			linnum++;
			break;

		    case '\r':
			if (input[p] == '\n')
			    continue;	// ignore
			c = '\n';	// treat EndOfLine as \n character
			linnum++;
			break;

		    case '"':
			t.len = stringbuffer.data.length();
			t.ustring = stringbuffer.data.toString();
			stringPostfix(t);
			return TOKstring;

		    case 0:
		    case 0x1A: {
				p--;
				problem("Unterminated string constant", IProblem.SEVERITY_ERROR, IProblem.UNTERMINATED_STRING_CONSTANT, token.ptr, p - token.ptr);
				
				t.ustring = "";
				t.len = 0;
				t.postfix = 0;
				return TOKstring;
		    }

		    default:
			if ((c & 0x80) != 0)
			{
			    p--;
			    c = decodeUTF();
			    if (c == LS || c == PS)
			    {	c = '\n';
				linnum++;
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

	    //printf("Lexer::charConstant\n");
	    p++;
	    c = input[p++];
	    switch (c)
	    {
		case '\\':
		    switch (input[p])
		    {
			case 'u':
			    t.numberValue = BigInteger.valueOf(escapeSequence());
			    tk = TOKwcharv;
			    break;

			case 'U':
			case '&':
			    t.numberValue = BigInteger.valueOf(escapeSequence());
			    tk = TOKdcharv;
			    break;

			default:
			    t.numberValue = BigInteger.valueOf(escapeSequence());
			    break;
		    }
		    break;

		case '\n':
		    linnum++;
		case '\r':
		case 0:
		case 0x1A:
		case '\'': {
		    problem("Unterminated character constant", IProblem.SEVERITY_ERROR, IProblem.UNTERMINATED_CHARACTER_CONSTANT, token.ptr, p - token.ptr);
		    return tk;
		}

		default:
		    if ((c & 0x80) != 0)
		    {
			p--;
			c = decodeUTF();
			p++;
			if (c == LS || c == PS) {
				linnum++;
				problem("Unterminated character constant", IProblem.SEVERITY_ERROR, IProblem.UNTERMINATED_CHARACTER_CONSTANT, token.ptr, p - token.ptr);
				return tk;
			}
			if (c < 0xD800 || (c >= 0xE000 && c < 0xFFFE))
			    tk = TOKwcharv;
			else
			    tk = TOKdcharv;
		    }
		    break;
	    }
	    
	    t.numberValue = BigInteger.valueOf(c);
	    t.len = p - t.ptr + 1;

	    if (input[p] != '\'')
	    {	
	    	problem("Unterminated character constant", IProblem.SEVERITY_ERROR, IProblem.UNTERMINATED_CHARACTER_CONSTANT, token.ptr, p - token.ptr);
	    	return tk;
	    }
	    p++;
	    return tk;
	}
	
	private void stringPostfix(Token t) {
		switch (input[p])
	    {
		case 'c':
		case 'w':
		case 'd':
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

		// printf("Lexer::number()\n");
		state = STATE.STATE_initial;
		base = 0;
		stringbuffer.reset();
		start = p;

	goto_done: 
		while (true) {
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
					p++;
					continue;

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
						p++;
						continue;
					}
					if (c == '.' && input[p + 1] != '.') {
						p = start;
						return inreal(t);
					} else if (c == 'i' || c == 'f' || c == 'F' || c == 'e'
							|| c == 'E') {
						// It's a real number. Back up and rescan as a real
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
						p++;
						continue;
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
						problem("Hex digit expected, not " + (char) c,
								IProblem.SEVERITY_ERROR,
								IProblem.HEX_DIGIT_EXPECTED, p, 1);
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
						p++;
						continue;
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
						p++;
						continue;
					}
					if (state == STATE.STATE_binary0) {
						problem("Binary digit expected",
								IProblem.SEVERITY_ERROR,
								IProblem.BINARY_DIGIT_EXPECTED, p, 1);
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
			stringbuffer.writeByte(c);
			p++;
		}
		
		if (state == STATE.STATE_octale) {
			problem("Octal digit expected", IProblem.SEVERITY_ERROR,
					IProblem.OCTAL_DIGIT_EXPECTED, p - 1, 1);
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
					problem("Unrecognized token",
							IProblem.SEVERITY_ERROR, IProblem.UNRECOGNIZED_TOKEN,
							p - 1, 1);
				}
				flags = (flags | f);
				continue;

			case 'l':
				// if (!global.params.useDeprecated)
				problem("'l' suffix is deprecated, use 'L' instead",
						IProblem.SEVERITY_ERROR, IProblem.L_SUFFIX_DEPRECATED,
						p, 1);
			case 'L':
				f = FLAGS_long;
				p++;
				if ((flags & f) != 0) {
					problem("Unrecognized token",
							IProblem.SEVERITY_ERROR, IProblem.UNRECOGNIZED_TOKEN,
							p - 1, 1);
				}
				flags = (flags | f);
				continue;
			default:
				break;
			}
			break;
		}

		if (integerOverflow) {
			problem("Integer overflow", IProblem.SEVERITY_ERROR,
					IProblem.INTEGER_OVERFLOW, t.ptr, p - start);
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
				problem("Signed integer overflow", IProblem.SEVERITY_ERROR,
						IProblem.SIGNED_INTEGER_OVERFLOW, start, p - start);
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
				problem("Signed integer overflow", IProblem.SEVERITY_ERROR,
						IProblem.SIGNED_INTEGER_OVERFLOW, start, p - start);
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
		t.numberValue = n;
		return result;
	}
	
	private TOK inreal(Token t) {
		int dblstate;
		int c;
		int hex; // is this a hexadecimal-floating-constant?
		TOK result;

		// printf("Lexer::inreal()\n");
		stringbuffer.reset();
		dblstate = 0;
		hex = 0;
	goto_done: 
		while (true) {
			// Get next char from input
			c = input[p++];
			// printf("dblstate = %d, c = '%c'\n", dblstate, c);
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
						problem("Binary-exponent-part required",
								IProblem.SEVERITY_ERROR,
								IProblem.BINARY_EXPONENT_PART_REQUIRED, p - 1, 1);
					}
					break goto_done;

				case 5: // looking immediately to right of E
					dblstate++;
					if (c == '-' || c == '+')
						break;
				case 6: // 1st exponent digit expected
					if (!Chars.isdigit(c)) {
						problem("Exponent expected", IProblem.SEVERITY_ERROR,
								IProblem.EXPONENT_EXPECTED, p - 1, 1);
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
		}
		p--;

		/*
		 * TODO: #if _WIN32 && __DMC__ char *save = __locale_decpoint;
		 * __locale_decpoint = "."; #endif #ifdef IN_GCC t->float80value =
		 * real_t::parse((char *)stringbuffer.data, real_t::LongDouble); #else
		 * t->float80value = strtold((char *)stringbuffer.data, NULL); #endif
		 * errno = 0;
		 */
		switch (input[p]) {
		case 'F':
		case 'f':
			/*
			 * TODO: #ifdef IN_GCC real_t::parse((char *)stringbuffer.data,
			 * real_t::Float); #else strtof((char *)stringbuffer.data, NULL);
			 * #endif
			 */
			result = TOKfloat32v;
			p++;
			break;

		default:
			/*
			 * TODO: #ifdef IN_GCC real_t::parse((char *)stringbuffer.data,
			 * real_t::Double); #else strtod((char *)stringbuffer.data, NULL);
			 * #endif
			 */
			result = TOKfloat64v;
			break;

		case 'l':
			// if (!global.params.useDeprecated)
			problem("'l' suffix is deprecated, use 'L' instead",
					IProblem.SEVERITY_ERROR, IProblem.L_SUFFIX_DEPRECATED,
					p, 1);
		case 'L':
			result = TOKfloat80v;
			p++;
			break;
		}
		if (input[p] == 'i' || input[p] == 'I') {
			if (input[p] == 'I') {
				problem("'I' suffix is deprecated, use 'i' instead",
						IProblem.SEVERITY_ERROR, IProblem.I_SUFFIX_DEPRECATED,
						p, 1);
			}
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
		 * TODO: #if _WIN32 && __DMC__ __locale_decpoint = save; #endif if
		 * (errno == ERANGE) error("number is not representable");
		 */
		return result;
	}
	
	private void pragma() {
		Token firstToken = new Token(token);
		Token tok = new Token();
	    int linnum;
	    String filespec = null;

	    scan(tok);
	    if (tok.value != TOKidentifier || !tok.ident.string.equals(Id.line)) {
	    	problem("#line integer [\"filespec\"]\\n expected", IProblem.SEVERITY_ERROR, IProblem.INVALID_PRAGMA_SYNTAX, firstToken.ptr, tok.ptr + tok.len - firstToken.ptr);
	    	return;
	    }

	    scan(tok);
	    if (tok.value == TOKint32v || tok.value == TOKint64v)
	    	linnum = tok.numberValue.intValue() - 1;
	    else {
	    	problem("#line integer [\"filespec\"]\\n expected", IProblem.SEVERITY_ERROR, IProblem.INVALID_PRAGMA_SYNTAX, firstToken.ptr, tok.ptr + tok.len - firstToken.ptr);
	    	return;
	    }

	    while (true)
	    {
		switch (input[p])
		{
		    case 0:
		    case 0x1A:
		    case '\n':
			this.linnum = linnum;
			// TODO
			//if (filespec != null)
			//    this.loc.filename = filespec;
			return;

		    case '\r':
			p++;
			if (input[p] != '\n')
			{   p--;
				this.linnum = linnum;
				// TODO
				//if (filespec != null)
				//    this.loc.filename = filespec;
				return;
			}
			continue;

		    case ' ':
		    case '\t':
		    // TODO: case '\v':
		    case '\f':
			p++;
			continue;			// skip white space

		    case '_':
			if (mod != null &&  p + 8 <= input.length 
					&& input[p + 0] == '_' 
					&& input[p + 1] == '_'
					&& input[p + 2] == 'F'
					&& input[p + 3] == 'I'
					&& input[p + 4] == 'L'
					&& input[p + 5] == 'E'
					&& input[p + 6] == '_'
					&& input[p + 7] == '_')
			{
			    p += 8;
			    if (mod.ident == null) {
			    	filespec = "TODO: this is not done in Descent... yet";
			    	// TODO
			    } else {
			    	// TODO
			    	// filespec = loc != null && loc.filename != null ? loc.filename : mod.ident.toString();
			    }
			}
			continue;

		    case '"':
			if (filespec != null) {
				problem("#line integer [\"filespec\"]\\n expected", IProblem.SEVERITY_ERROR, IProblem.INVALID_PRAGMA_SYNTAX, firstToken.ptr, tok.ptr + tok.len - firstToken.ptr);
				return;
			}
			stringbuffer.reset();
			p++;
			while (true)
			{   int c;

			    c = input[p];
			    switch (c)
			    {
				case '\n':
				case '\r':
				case 0:
				case 0x1A: {
					problem("#line integer [\"filespec\"]\\n expected", IProblem.SEVERITY_ERROR, IProblem.INVALID_PRAGMA_SYNTAX, firstToken.ptr, tok.ptr + tok.len - firstToken.ptr);
					return;
				}

				case '"':
				    filespec = stringbuffer.data.toString();
				    p++;
				    break;

				default:
				    if ((c & 0x80) != 0)
				    {   int u = decodeUTF();
					if (u == PS || u == LS) {
						problem("#line integer [\"filespec\"]\\n expected", IProblem.SEVERITY_ERROR, IProblem.INVALID_PRAGMA_SYNTAX, firstToken.ptr, tok.ptr + tok.len - firstToken.ptr);
						return;
					}
				    }
				    stringbuffer.writeByte(c);
				    p++;
				    continue;
			    }
			    break;
			}
			continue;

		    default:
			if ((input[p] & 0x80) != 0)
			{   int u = decodeUTF();
			    if (u == PS || u == LS) {
			    	this.linnum = linnum;
			    	// TODO:
					//if (filespec != null)
					//    this.loc.filename = filespec;
					return;
			    }
			}
		    problem("#line integer [\"filespec\"]\\n expected", IProblem.SEVERITY_ERROR, IProblem.INVALID_PRAGMA_SYNTAX, firstToken.ptr, tok.ptr + tok.len - firstToken.ptr);
		    return;
		}
	    }
	}
	
	private int decodeUTF() {
		int[] u = new int[] { 0 };
	    int s = p;
	    int len;
	    int[] idx;
	    String msg = null;

	    // Check length of remaining string up to 6 UTF-8 characters
	    for (len = 1; len < 6 && len < end; len++)
		;

	    idx = new int[] { 0 };
	    msg = Utf.decodeChar(input, s, len, idx, u);
	    p += idx[0] - 1;
	    if (msg != null)
	    {
	    	problem(msg, IProblem.SEVERITY_ERROR, IProblem.INVALID_UTF_8_SEQUENCE, p, 1);
	    }
	    return u[0];
	}
	
	private void getDocComment(Token t, boolean lineComment) {
		OutBuffer buf = new OutBuffer();
	    char ct = input[t.ptr + 2];
	    int q = t.ptr + 3;	// start of comment text
	    int linestart = 0;

	    int qend = p;
	    if (ct == '*' || ct == '+')
		qend -= 2;

	    /* Scan over initial row of ****'s or ++++'s or ////'s
	     */
	    for (; q < qend; q++)
	    {
		if (input[q] != ct)
		    break;
	    }

	    /* Remove trailing row of ****'s or ++++'s
	     */
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
			    /* Trim preceding whitespace up to preceding \n
			     */
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

				/* Trim trailing whitespace
				 */
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

				/* Trim trailing whitespace
				 */
				while (buf.data.length() > 0 && (buf.data.charAt(buf.data.length() - 1) == ' ' || buf.data.charAt(buf.data.length() - 1) == '\t'))
					buf.data.deleteCharAt(buf.data.length() - 1);
				break;
			    }
			}
			linestart = 0;
			break;

		    case '\n':
			linestart = 1;

			/* Trim trailing whitespace
			 */
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
	    	if (t.blockComment != null) {
	    		t.blockCommentLength = p - 1;
	    		t.blockComment = combineComments(t.blockComment, buf.data.toString());
	    	} else {
	    		t.blockCommentPtr = t.ptr;
	    		t.blockCommentLength = p - 1;
	    		t.blockComment = buf.data.toString().trim();
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
	
	public static Map<String, TOK> keywords;
	
	static {
		keywords = new Hashtable<String, TOK>();
		keywords.put("this", TOKthis);
		keywords.put("super", TOKsuper);
		keywords.put("assert", TOKassert);
		keywords.put("null", TOKnull);
		keywords.put("true", TOKtrue);
		keywords.put("false", TOKfalse);
		keywords.put("cast", TOKcast);
		keywords.put("new", TOKnew);
		keywords.put("delete", TOKdelete);
		keywords.put("throw", TOKthrow);
		keywords.put("module", TOKmodule);
		keywords.put("pragma", TOKpragma);
		keywords.put("typeof", TOKtypeof);
		keywords.put("typeid", TOKtypeid);
		keywords.put("iftype", TOKiftype);
		keywords.put("template", TOKtemplate);
		keywords.put("void", TOKvoid);
		keywords.put("byte", TOKint8);
		keywords.put("ubyte", TOKuns8);

		keywords.put("short", TOKint16);
		keywords.put("ushort", TOKuns16);
		keywords.put("int",	 TOKint32);
		keywords.put("uint", TOKuns32);
		keywords.put("long", TOKint64);
		keywords.put("ulong", TOKuns64);
	    keywords.put("cent", TOKcent);
	    keywords.put("ucent", TOKucent);
	    keywords.put("float", TOKfloat32);
	    keywords.put("double", TOKfloat64);
	    keywords.put("real", TOKfloat80);

	    keywords.put("bit", TOKbit);
	    keywords.put("bool", TOKbool);
	    keywords.put("char", TOKchar);
	    keywords.put("wchar", TOKwchar);
	    keywords.put("dchar", TOKdchar);

	    keywords.put("ifloat", TOKimaginary32);
	    keywords.put("idouble", TOKimaginary64);
	    keywords.put("ireal", TOKimaginary80);

	    keywords.put("cfloat", TOKcomplex32);
	    keywords.put("cdouble", TOKcomplex64);
	    keywords.put("creal", TOKcomplex80);

	    keywords.put("delegate", TOKdelegate);
	    keywords.put("function", TOKfunction);

	    keywords.put("is", TOKis);
	    keywords.put("if", TOKif);
	    keywords.put("else", TOKelse);
	    keywords.put("while", TOKwhile);
	    keywords.put("for", TOKfor);
	    keywords.put("do", TOKdo);
	    keywords.put("switch", TOKswitch);
	    keywords.put("case", TOKcase);
	    keywords.put("default", TOKdefault);
	    keywords.put("break", TOKbreak);
	    keywords.put("continue", TOKcontinue);
	    keywords.put("synchronized", TOKsynchronized);
	    keywords.put("return", TOKreturn);
	    keywords.put("goto", TOKgoto);
	    keywords.put("try", TOKtry);
	    keywords.put("catch", TOKcatch);
	    keywords.put("finally", TOKfinally);
	    keywords.put("with", TOKwith);
	    keywords.put("asm", TOKasm);
	    keywords.put("foreach", TOKforeach);
	    keywords.put("foreach_reverse", TOKforeach_reverse);
	    keywords.put("scope", TOKscope);
	    keywords.put("on_scope_exit", TOKon_scope_exit);
	    keywords.put("on_scope_failure", TOKon_scope_failure);
	    keywords.put("on_scope_success", TOKon_scope_success);

	    keywords.put("struct", TOKstruct);
	    keywords.put("class", TOKclass);
	    keywords.put("interface", TOKinterface);
	    keywords.put("union", TOKunion);
	    keywords.put("enum", TOKenum);
	    keywords.put("import", TOKimport);
	    keywords.put("mixin", TOKmixin);
	    keywords.put("static", TOKstatic);
	    /*{	"virtual",	TOKvirtual	},*/
	    keywords.put("final", TOKfinal);
	    keywords.put("const", TOKconst);
	    keywords.put("typedef", TOKtypedef);
	    keywords.put("alias", TOKalias);
	    keywords.put("override", TOKoverride);
	    keywords.put("abstract", TOKabstract);
	    keywords.put("volatile", TOKvolatile);
	    keywords.put("debug", TOKdebug);
	    keywords.put("deprecated", TOKdeprecated);
	    keywords.put("in", TOKin);
	    keywords.put("out", TOKout);
	    keywords.put("inout", TOKinout);
	    keywords.put("lazy", TOKlazy);
	    keywords.put("auto", TOKauto);

	    keywords.put("align", TOKalign);
	    keywords.put("extern", TOKextern);
	    keywords.put("private", TOKprivate);
	    keywords.put("package", TOKpackage);
	    keywords.put("protected", TOKprotected);
	    keywords.put("public", TOKpublic);
	    keywords.put("export", TOKexport);

	    keywords.put("body", TOKbody	);
	    keywords.put("invariant", TOKinvariant);
	    keywords.put("unittest", TOKunittest);
	    keywords.put("version", TOKversion);
	}
	
	private void initKeywords() {
		StringValue sv;
		for(Map.Entry<String, TOK> entry : keywords.entrySet()) {
			sv = stringtable.insert(entry.getKey());
			sv.ptrvalue = new Identifier(sv.lstring, entry.getValue());
		}
	}

}
