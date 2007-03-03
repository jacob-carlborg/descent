package descent.core.dom;

import descent.core.compiler.CharOperation;
import descent.core.compiler.IScanner;
import descent.core.compiler.InvalidInputException;
import descent.internal.core.parser.TOK;

/**
 * Implementation of an IScanner for the D language.
 * 
 * It wraps the internal Lexer class.
 */
public class PublicScanner implements IScanner {
	
	private final static char[] EMPTY_CHAR_ARRAY = CharOperation.NO_CHAR;
	private final static String EMPTY_STRING = "";
	
	private final boolean tokenizeComments;
	private final boolean tokenizePragmas;
	private final boolean tokenizeWhiteSpace;	
	private final boolean recordLineSeparator;
	private final int apiLevel;
	private char[] source;
	public Lexer lexer;

	public PublicScanner(boolean tokenizeComments, boolean tokenizePragmas, boolean tokenizeWhiteSpace, boolean recordLineSeparator, int apiLevel) {
		this.tokenizeComments = tokenizeComments;
		this.tokenizePragmas = tokenizePragmas;
		this.tokenizeWhiteSpace = tokenizeWhiteSpace;		
		this.recordLineSeparator = recordLineSeparator;
		this.apiLevel = apiLevel;
	}

	public int getCurrentTokenEndPosition() {
		if (lexer.token.value == TOK.TOKeof) {
			return lexer.token.ptr + lexer.token.len;
		} else {
			return lexer.token.ptr + lexer.token.len - 1;
		}
	}

	public int getCurrentTokenStartPosition() {
		return lexer.token.ptr;
	}
	
	// This method is here to make DefaultCommentMapper look much more like
	// the original one
	public int getCurrentPosition() {
		return getCurrentTokenEndPosition() + 1;
	}

	public int getLineEnd(int lineNumber) {
		if (lineNumber <= 0) {
			return -1;
		}
		if (lineNumber - 1 < lexer.lineEnds.size()) {
			return lexer.lineEnds.get(lineNumber - 1);
		}
		return lexer.end;
	}

	public int[] getLineEnds() {
		return lexer.getLineEnds();
	}

	public int getLineNumber(int position) {
		if (lexer.lineEnds.size() == 0)
			return 1;
		int length = lexer.lineEnds.size();
		if (length == 0)
			return 1;
		int g = 0, d = length - 1;
		int m = 0;
		while (g <= d) {
			m = (g + d) /2;
			if (position < lexer.lineEnds.get(m)) {
				d = m-1;
			} else if (position > lexer.lineEnds.get(m)) {
				g = m+1;
			} else {
				return m + 1;
			}
		}
		if (position < lexer.lineEnds.get(m)) {
			return m+1;
		}
		return m+2;
	}

	public int getLineStart(int lineNumber) {
		if (lineNumber <= 0) {
			return -1;
		}
		if (lineNumber == 1) {
			return lexer.base;
		}
		if (lineNumber - 1 <= lexer.lineEnds.size()) {
			return lexer.lineEnds.get(lineNumber - 2) + 1;
		}
		return -1;
	}

	public int getNextToken() throws InvalidInputException {
		return lexer.nextToken().terminalSymbol;
	}

	public char[] getRawTokenSource() {
		switch(lexer.token.value) {
			case TOKeof:
				return EMPTY_CHAR_ARRAY;
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
				return lexer.token.string.toCharArray();
			case TOKidentifier:
				return lexer.token.ident.string.toCharArray();
			default:
				return lexer.token.value.charArrayValue;
		}		
	}
	
	public String getRawTokenSourceAsString() {
		switch(lexer.token.value) {
		case TOKeof:
			return EMPTY_STRING;
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
			return lexer.token.string;
		case TOKidentifier:
			return lexer.token.ident.string;
		default:
			return lexer.token.value.value;
	}	
	}

	public char[] getSource() {
		return source;
	}

	public void resetTo(int startPosition, int endPosition) {
		if (this.lexer == null) {
			this.lexer = new Lexer(source, startPosition, endPosition - startPosition, tokenizeComments, tokenizePragmas, tokenizeWhiteSpace, recordLineSeparator, apiLevel);
		} else {
			this.lexer.reset(startPosition, endPosition - startPosition);
		}
	}

	public void setSource(char[] source) {
		this.source = source;
		if (this.lexer == null) {
			this.lexer = new Lexer(source, 0, source.length, tokenizeComments, tokenizePragmas, tokenizeWhiteSpace, recordLineSeparator, apiLevel);
		} else {
			this.lexer.reset(source, 0, source.length, tokenizeComments, tokenizePragmas, tokenizeWhiteSpace, recordLineSeparator);
		}
		resetTo(0, source.length);
	}
	
	/**
	 * This method allows this scanner to reuse the lexer that is used
	 * to parse a compilation unit.
	 */
	public void setLexerAndSource(Lexer lexer, char[] source) {
		this.lexer = lexer;
		this.source = source;
	}

}
