package mmrnmhrm.ui.text;

import melnorme.miscutil.AssertIn;
import melnorme.miscutil.ExceptionAdapter;
import melnorme.miscutil.log.Logg;
import mmrnmhrm.ui.text.color.IDeeColorPreferences;
import mmrnmhrm.ui.text.color.TextAttributeRegistry;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.util.PropertyChangeEvent;

import descent.core.dom.AST;
import descent.core.domX.TokenUtil;
import descent.internal.compiler.parser.Lexer;
import descent.internal.compiler.parser.TOK;
import descent.internal.compiler.parser.Token;

/**
 * D Code Scanner. Uses DMD's lexer to do the text attribute tokenization.
 */
public class DeeCodeScanner implements ITokenScanner {
	
	private TextAttributeRegistry manager;
	private Lexer lexer;
	private boolean whitespace;
	//private Token token;
	//private TOK tok;
	IDocument document;
	private int wsoffset;
	private int wslength;
	private int rangeOffset;


	public DeeCodeScanner() {
		manager = TextAttributeRegistry.getDefault();
		loadDeeTokens();
		lexer = new Lexer("", 0, 0, false, false, false, false, AST.D2);
	}
	
	/** Updates the color according to current preferences. */
	private void loadDeeTokens() {
		manager.loadToken(IDeeColorPreferences.DEE_SPECIAL);
		manager.loadToken(IDeeColorPreferences.DEE_STRING);
		manager.loadToken(IDeeColorPreferences.DEE_LITERALS);
		manager.loadToken(IDeeColorPreferences.DEE_OPERATORS);
		manager.loadToken(IDeeColorPreferences.DEE_BASICTYPES);
		manager.loadToken(IDeeColorPreferences.DEE_KEYWORD);
		manager.loadToken(IDeeColorPreferences.DEE_DOCCOMMENT);
		manager.loadToken(IDeeColorPreferences.DEE_COMMENT);
		manager.loadToken(IDeeColorPreferences.DEE_DEFAULT);
	}

	public void setRange(IDocument document, int offset, int length) {
		Logg.codeScanner.println(">> DeeCodeScanner#setRange: " + offset + ","+ length);
		AssertIn.isTrue(offset >= 0 && length >= 1);
		AssertIn.isTrue(offset + length <= document.get().length());
		try {
			Logg.codeScanner.println(document.get(offset, length) );
			char[] srcAr = document.get(offset, length).toCharArray();
			lexer.reset(srcAr, 0, length, true, true, true, true);
		} catch (BadLocationException e) {
			throw ExceptionAdapter.unchecked(e);
		}
		//lexer = new Lexer(document.get(), offset, length, true, true, false, true, 1);

		this.rangeOffset = offset;
		this.wsoffset = offset;
		this.document = document;
	}
	
	public int getTokenLength() {
		if(whitespace) {
			return wslength;
		} else {
			return lexer.token.len;
		}
	}

	public int getTokenOffset() {
		if(whitespace) {
			return wsoffset;
		} else {
			return getRealTokenOffset(lexer.token);
		}
	}

	private int getRealTokenOffset(Token token) {
		return token.ptr + rangeOffset;
	}

	public IToken nextToken() {

		if(whitespace) {
			// Don't fetch a new token, use current one.
			whitespace = false;
		} else {
			lexer.nextToken();
		}
		
		Token token = lexer.token;
		int tokenOffset = getRealTokenOffset(token) ;

		int endpos = wsoffset + wslength;
		if(tokenOffset > endpos) {
			whitespace = true;
			wsoffset = endpos;
			wslength = tokenOffset - wsoffset;
			return org.eclipse.jface.text.rules.Token.WHITESPACE;
		}
		String offsetStr = String.format("%3d", tokenOffset);
		String lenStr = String.format("%2d", token.len);
		Logg.codeScanner.println("Token["+offsetStr +","+ lenStr +"]: " +token);
		
		wsoffset = tokenOffset;
		wslength = token.len;
		
		return getTextAttributeForToken(token.value);
	}

	private IToken getTextAttributeForToken(TOK tok) {
		
		if(tok == TOK.TOKeof)
			return org.eclipse.jface.text.rules.Token.EOF;
		
		if(tok == TOK.TOKwhitespace)
			return org.eclipse.jface.text.rules.Token.WHITESPACE;

		IToken tatoken;
		if(tok == TOK.TOKreserved) {
			tatoken = manager.getToken(IDeeColorPreferences.DEE_SPECIAL);
			if(tatoken != null)	return tatoken;
		}
		if(tok == TOK.TOKstring) {
			tatoken = manager.getToken(IDeeColorPreferences.DEE_STRING);
			if(tatoken != null)	return tatoken;
		}
		if(TokenUtil.isLiteral(tok)) {
			tatoken = manager.getToken(IDeeColorPreferences.DEE_LITERALS);
			if(tatoken != null)	return tatoken;
		}
		if(TokenUtil.isBasicType(tok)) {
			tatoken = manager.getToken(IDeeColorPreferences.DEE_BASICTYPES);
			if(tatoken != null)	return tatoken;
		}
		if(TokenUtil.isKeyword(tok)) {
			tatoken = manager.getToken(IDeeColorPreferences.DEE_KEYWORD);
			if(tatoken != null)	return tatoken;
		}
		if(TokenUtil.isDDocComment(tok)) {
			tatoken = manager.getToken(IDeeColorPreferences.DEE_DOCCOMMENT);
			if(tatoken != null)	return tatoken;
		}
		if(TokenUtil.isSimpleComment(tok)) {
			tatoken = manager.getToken(IDeeColorPreferences.DEE_COMMENT);
			if(tatoken != null)	return tatoken;
		}
		/*if(TokenUtil.isOperator(tok)) {
			if(true) return manager.getToken(IDeeColorPreferences.DEE_DEFAULT);
			tatoken = manager.getToken(IDeeColorPreferences.DEE_OPERATORS);
			if(tatoken != null)	return tatoken;
		}*/
		{
			tatoken = manager.getToken(IDeeColorPreferences.DEE_DEFAULT);
			if(tatoken != null)	return tatoken;
		}
		
		return TextAttributeRegistry.DEFAULT_TOKEN;
	}

	
	public String getTokenStr(Token token) {
		try {
			return document.get(token.ptr, token.len);
		} catch (BadLocationException e) {
			// TO DO: check the exception
			throw ExceptionAdapter.unchecked(e);
		}
	}

	public boolean adaptToPreferenceChange(PropertyChangeEvent event) {
		String prop = event.getProperty();
		if(prop.startsWith(IDeeColorPreferences.PREFIX)) {
			loadDeeTokens();
			return true;
		}
		return false;
	}

}
