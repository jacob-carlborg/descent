package dtool.formater;

import static descent.internal.core.dom.TOK.TOKcomment;
import static descent.internal.core.dom.TOK.TOKeof;
import descent.internal.core.dom.TOK;
import descent.internal.core.dom.Token;

public class ScannerHelper {
	public static final int EOF = -1;

	public int pos; // offset
	private char[] input;
	private FormaterScribe scribe;

	ScannerHelper(FormaterScribe scribe, int filepos, char[] sourcebuf ) {
		this.scribe = scribe;
		this.pos = filepos;
		this.input = sourcebuf;
	}

	/** Lookahead */
	private int LA(int la) {
		int lapos = pos + la - 1;
		if (lapos >= input.length)
			return EOF;
		else
			return input[lapos];
	}
	
	private int nextchar() {
		if (pos >= input.length)
			return EOF;
		else
			return input[pos++];
	}

	private boolean matchNewLine() {
		int nchars = matchesNewLine();
		if (nchars > 0) {
			pos += nchars;
			return true;
		}
		return false;
	}
	
	private void eventNewLine(int startpos, int endpos) {
		scribe.eventNewLine(endpos);
	}

	private int matchesNewLine() {
		char nchars = 0;

		switch (LA(1)) {
		case '\r':
			if (LA(2) != '\n') {
				nchars = 2;
				eventNewLine(pos, pos + nchars);
			} else {
				nchars = 1;
				eventNewLine(pos, pos + nchars);
			}
			break;

		case '\n':
			nchars = 1;
			eventNewLine(pos, pos + nchars);
			break;

		}
		return nchars;
	}

	public Token nextToken() {
		Token tok = new Token();
		tok.ptr = pos;

		while (true) {

			if (matchNewLine()) {
				tok.value = TOK.TOK_whitespace;
				return tok;
			}

			switch (nextchar()) {
			case 0:
			case 0x1A: case EOF:
				tok.value = TOKeof; // end of file
				tok.len = 0;
				return tok;

			case ' ':
			case '\t':
			// TODO: case '\v':
			case '\f':
				tok.value = TOK.TOK_whitespace;
				return tok;

			case '/':
				switch (nextchar()) {

				case '/': // do // style comments
					while (true) {

						if (matchNewLine() || nextchar() == EOF)
							break;
					}

					tok.value = TOKcomment;
					return tok;

				case '*': // FIXME: ML comments
				case '+':
					while (true) {
						
						if (matchNewLine())
							break;

						switch (nextchar()) {

						case '*':
						case '+':
							if(LA(1) == '/') {
								nextchar();
								break; // will break through
							}

						default:
							continue;
						}
						break;
					}

					tok.value = TOKcomment;
					return tok;
				}
			default:
				tok.value = TOK.TOK_unknown;
				pos--;
				return tok;
			}
		}
	}

	public boolean isWhiteSpace(Token tok) {
		return tok.value == TOK.TOK_whitespace;
	}

	public boolean isSLComment(Token tok) {
		return tok.value == TOK.TOKcomment && input[tok.ptr+1] == '/';
	}

	public boolean isMLComment(Token tok) {
		return tok.value == TOK.TOKcomment && input[tok.ptr+1] == '*';
	}

	public boolean isNestedComment(Token tok) {
		return tok.value == TOK.TOKcomment && input[tok.ptr+1] == '+';
	}

}
