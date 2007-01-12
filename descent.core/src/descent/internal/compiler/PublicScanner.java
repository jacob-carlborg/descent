package descent.internal.compiler;

import descent.core.compiler.IScanner;
import descent.core.compiler.InvalidInputException;
import descent.core.dom.AST;
import descent.core.dom.Lexer;

/**
 * Implementation of an IScanner for the D language.
 * 
 * It wraps the internal Lexer class.
 */
public class PublicScanner implements IScanner {
	
	private final boolean tokenizeComments;
	private final boolean tokenizeWhiteSpace;
	private final boolean recordLineSeparator;
	private final int apiLevel;
	private int startPosition;
	private int endPosition;
	private char[] source;
	private Lexer lexer;	

	public PublicScanner(boolean tokenizeComments, boolean tokenizeWhiteSpace, boolean recordLineSeparator, int apiLevel) {
		this.tokenizeComments = tokenizeComments;
		this.tokenizeWhiteSpace = tokenizeWhiteSpace;
		this.recordLineSeparator = recordLineSeparator;
		this.apiLevel = apiLevel;
	}

	public int getCurrentTokenEndPosition() {
		return lexer.token.ptr + lexer.token.len;
	}

	public int getCurrentTokenStartPosition() {
		return lexer.token.ptr;
	}

	public int getLineEnd(int lineNumber) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int[] getLineEnds() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getLineNumber(int charPosition) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getLineStart(int lineNumber) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getNextToken() throws InvalidInputException {
		if (lexer == null) {
			lexer = new Lexer(AST.newAST(AST.D1), source, startPosition, endPosition);
		}
		return lexer.nextToken().terminalSymbol;
	}

	public char[] getRawTokenSource() {
		return lexer.token.value.value.toCharArray();
	}

	public char[] getSource() {
		return source;
	}

	public void resetTo(int startPosition, int endPosition) {
		this.startPosition = startPosition;
		this.endPosition = endPosition;
		lexer = null;
	}

	public void setSource(char[] source) {
		this.source = source;
		resetTo(0, source.length);
	}

}
