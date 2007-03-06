package mmrnmhrm.text;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;

import util.AssertIn;
import util.Logg;

import descent.internal.core.dom.Lexer;
import descent.internal.core.dom.TOK;
import descent.internal.core.dom.Token;

public class DeeCodeScanner_Native implements ITokenScanner {
	Lexer lexer;

	public void setRange(IDocument document, int offset, int length) {
		// TODO: don't allocate a new Lexer, re-use ?
		Logg.println("CodeScanner#setRange: " + offset + ","+ length);
		AssertIn.isTrue(offset >= 0 && length >= 1);
		AssertIn.isTrue(offset + length <= document.get().length());
		lexer = new Lexer(document.get(), offset, offset+length, true, true);
	}
	
	public int getTokenLength() {
		return lexer.token.len;
	}

	public int getTokenOffset() {
		return lexer.token.ptr;
	}

	public IToken nextToken() {
		TOK tok = lexer.nextToken();
		Token token = lexer.token;
		Logg.println("Token: " + token +"["+ token.ptr +","+ token.len +"]");
		if(tok == TOK.TOKeof)
			return EOFToken.getDefault();

		return DeeCodeHighlightOptions.getDefault().getAttributes(tok);
		
	}


}
