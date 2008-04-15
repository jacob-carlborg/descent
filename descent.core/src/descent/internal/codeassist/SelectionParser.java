package descent.internal.codeassist;

import static descent.internal.compiler.parser.TOK.TOKblockcomment;
import static descent.internal.compiler.parser.TOK.TOKdocblockcomment;
import static descent.internal.compiler.parser.TOK.TOKdoclinecomment;
import static descent.internal.compiler.parser.TOK.TOKdocpluscomment;
import static descent.internal.compiler.parser.TOK.TOKlinecomment;
import static descent.internal.compiler.parser.TOK.TOKpluscomment;
import descent.internal.compiler.parser.Parser;
import descent.internal.compiler.parser.TOK;
import descent.internal.compiler.parser.Token;

public class SelectionParser extends Parser {
	
	/**
	 * If the selection is inside a comment token, this holds it.
	 */
	Token commentToken;
	
	int selectionOffset;
	int selectionLength;

	public SelectionParser(int apiLevel,char[] source, char[] filename) {
		super(source, 0, source.length,
				true, false, false, false,
				apiLevel,
				null, null, false,
				filename);
		this.diet = true;
	}
	
	/*
	 * Do diet parsing, and return false if the selection location
	 * falls in a function.
	 */
	@Override
	protected boolean dietParse() {
		int before = token.ptr + token.sourceLen;
		
		boolean ret = super.dietParse();
		
		int after = token.ptr + token.sourceLen;
		
		//       |---------|
		//     before     after
		//
		//
		//    |------------------|
		//  offset            offset + length
		//
		// If any of these points fall between the other two in the top or bottom,
		// there is an intersection.
		
		if (isBetween(selectionOffset, before, selectionOffset + selectionLength) ||
			isBetween(selectionOffset, after, selectionOffset + selectionLength) ||
			isBetween(before, selectionOffset, after) ||
			isBetween(before, selectionOffset + selectionLength, after)) {
			return false;
		}
		
		return ret;
	}
	
	private boolean isBetween(int min, int val, int max) {
		return min <= val && val <= max;
	}
	
	@Override
	public TOK nextToken() {
		TOK tok = Lexer_nextToken();

		while ((tok == TOKlinecomment || tok == TOKdoclinecomment
				|| tok == TOKblockcomment
				|| tok == TOKdocblockcomment
				|| tok == TOKpluscomment || tok == TOKdocpluscomment)) {
			if (token.ptr <= selectionOffset
					&& selectionOffset <= token.ptr + token.sourceLen) {
				this.commentToken = new Token(token);
			}

			tok = Lexer_nextToken();
		}

		return tok;
	}

}
