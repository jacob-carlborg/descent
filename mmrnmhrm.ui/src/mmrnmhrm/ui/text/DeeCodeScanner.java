package mmrnmhrm.ui.text;

import melnorme.miscutil.ArrayUtil;
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

import descent.core.compiler.DeeToken;
import descent.internal.core.dom.Lexer;
import descent.internal.core.dom.TOK;
import descent.internal.core.dom.Token;

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


	public DeeCodeScanner() {
		manager = TextAttributeRegistry.getDefault();
		loadDeeTokens();
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
		Logg.codeScanner.println("DeeCodeScanner#setRange: " + offset + ","+ length);
		AssertIn.isTrue(offset >= 0 && length >= 1);
		AssertIn.isTrue(offset + length <= document.get().length());
		try {
			Logg.codeScanner.println(" " + document.get(offset, length) );
		} catch (BadLocationException e) {
			throw ExceptionAdapter.unchecked(e);
		}
		// TODO: don't allocate a new Lexer, re-use ?
		lexer = new Lexer(document.get(), offset, offset+length, true, true);
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
			return lexer.token.ptr;
		}
	}
	
	

	public IToken nextToken() {

		if(whitespace) {
			// Don't fetch a new token, use current one.
			whitespace = false;
		} else {
			lexer.nextToken();
		}
		
		Token token = lexer.token;

		int endpos = wsoffset + wslength;
		if(token.ptr > endpos) {
			whitespace = true;
			wsoffset = endpos;
			wslength = token.ptr - wsoffset;
			return org.eclipse.jface.text.rules.Token.WHITESPACE;
		}
		Logg.codeScanner.println("Token: " +token+ "["+ token.ptr +","+ token.len +"]");
		
		wsoffset = token.ptr;
		wslength = token.len;
		
		return getTextAttributeForToken(token);
		
	}

	private IToken getTextAttributeForToken(Token token) {
		TOK tok = token.value;
		
		if(tok == TOK.TOKeof)
			return org.eclipse.jface.text.rules.Token.EOF;

		IToken tatoken;
		if(tok == TOK.TOKreserved) {
			tatoken = manager.getToken(IDeeColorPreferences.DEE_SPECIAL);
			if(tatoken != null)	return tatoken;
		}
		if(tok == TOK.TOKstring) {
			tatoken = manager.getToken(IDeeColorPreferences.DEE_STRING);
			if(tatoken != null)	return tatoken;
		}
		if(ArrayUtil.contains(TOK.literals, tok)) {
			tatoken = manager.getToken(IDeeColorPreferences.DEE_LITERALS);
			if(tatoken != null)	return tatoken;
		}
		if(ArrayUtil.contains(TOK.operators, tok)) {
			tatoken = manager.getToken(IDeeColorPreferences.DEE_OPERATORS);
			if(tatoken != null)	return tatoken;
		}
		if(ArrayUtil.contains(TOK.basicTypes, tok)) {
			tatoken = manager.getToken(IDeeColorPreferences.DEE_BASICTYPES);
			if(tatoken != null)	return tatoken;
		}
		if(Lexer.keywords.containsValue(tok)) {
			tatoken = manager.getToken(IDeeColorPreferences.DEE_KEYWORD);
			if(tatoken != null)	return tatoken;
		}
		if(DeeToken.isDDocComment(getTokenStr(token), token)) {
			tatoken = manager.getToken(IDeeColorPreferences.DEE_DOCCOMMENT);
			if(tatoken != null)	return tatoken;
		}
		if(DeeToken.isSimpleComment(getTokenStr(token), token)) {
			tatoken = manager.getToken(IDeeColorPreferences.DEE_COMMENT);
			if(tatoken != null)	return tatoken;
		}
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
