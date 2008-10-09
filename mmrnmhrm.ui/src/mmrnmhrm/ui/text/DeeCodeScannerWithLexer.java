package mmrnmhrm.ui.text;

import java.util.List;

import melnorme.miscutil.AssertIn;
import melnorme.miscutil.ExceptionAdapter;
import mmrnmhrm.ui.text.color.IDeeColorConstants;
import mmrnmhrm.ui.text.color.LangColorPreferences;

import org.eclipse.dltk.ui.text.AbstractScriptScanner;
import org.eclipse.dltk.ui.text.IColorManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;

import descent.core.dom.AST;
import descent.internal.compiler.parser.Lexer;
import descent.internal.compiler.parser.TOK;
import descent.internal.compiler.parser.Token;
import descent.internal.compiler.parser.ast.TokenUtil;
import dtool.Logg;

@Deprecated
public class DeeCodeScannerWithLexer extends AbstractScriptScanner {
	
	public static org.eclipse.jface.text.rules.Token DEFAULT_TOKEN = 
		new org.eclipse.jface.text.rules.Token(new TextAttribute(null));


	//private TextAttributeRegistry manager;
	private Lexer lexer;
	private boolean whitespace;
	//private Token token;
	//private TOK tok;
	IDocument document;
	private int lastoffset;
	private int lastlength;
	private int rangeOffset;

	
	public DeeCodeScannerWithLexer(IColorManager manager, IPreferenceStore store) {
		super(manager, store);
		initialize();
		lexer = new Lexer("", 0, 0, false, false, false, false, AST.D2);
	}
	
	private static String fgTokenProperties[] = new String[] {
		IDeeColorConstants.DEE_SPECIAL,
		IDeeColorConstants.DEE_STRING,
		IDeeColorConstants.DEE_LITERALS,
		IDeeColorConstants.DEE_OPERATORS,
		IDeeColorConstants.DEE_BASICTYPES,
		IDeeColorConstants.DEE_KEYWORD,
		IDeeColorConstants.DEE_DOCCOMMENT,
		IDeeColorConstants.DEE_COMMENT,
		IDeeColorConstants.DEE_DEFAULT
	};

	@Override
	protected String[] getTokenProperties() {
		return fgTokenProperties;
	}

	@Override
	protected List<IRule> createRules() {
		return null;
	}
	
	@Override
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
		this.lastoffset = offset;
		this.document = document;
	}
	
	@Override
	public int getTokenLength() {
		if(whitespace) {
			return lastlength;
		} else {
			return lexer.token.len;
		}
	}

	@Override
	public int getTokenOffset() {
		if(whitespace) {
			return lastoffset;
		} else {
			return getRealTokenOffset(lexer.token);
		}
	}

	private int getRealTokenOffset(Token token) {
		return token.ptr + rangeOffset;
	}

	@Override
	public IToken nextToken() {

		if(whitespace) {
			// Don't fetch a new token, use current one.
			whitespace = false;
		} else {
			lexer.nextToken();
		}
		
		Token token = lexer.token;
		int tokenOffset = getRealTokenOffset(token) ;

		int lastEnd = lastoffset + lastlength;

		if(lastEnd > tokenOffset) {
			return nextToken();
		}
		
		if(tokenOffset > lastEnd) {
			whitespace = true;
			lastoffset = lastEnd;
			lastlength = tokenOffset - lastoffset;
			return org.eclipse.jface.text.rules.Token.WHITESPACE;
		} 
		String offsetStr = String.format("%3d", tokenOffset);
		String lenStr = String.format("%2d", token.len);
		Logg.codeScanner.println("Token["+offsetStr +","+ lenStr +"]: " +token);
		
		lastoffset = tokenOffset;
		lastlength = token.len;
		
		return getTextAttributeForToken(token.value);
	}

	private IToken getTextAttributeForToken(TOK tok) {
		
		if(tok == TOK.TOKeof)
			return org.eclipse.jface.text.rules.Token.EOF;
		
		if(tok == TOK.TOKwhitespace)
			return org.eclipse.jface.text.rules.Token.WHITESPACE;

		if(tok == TOK.TOKstring && isEnabled(IDeeColorConstants.DEE_STRING)) {
			return getToken(IDeeColorConstants.DEE_STRING);
		}
		if(TokenUtil.isLiteral(tok) && isEnabled(IDeeColorConstants.DEE_LITERALS)) {
			return getToken(IDeeColorConstants.DEE_LITERALS);
		}
		if(TokenUtil.isBasicType(tok) && isEnabled(IDeeColorConstants.DEE_BASICTYPES)) {
			return getToken(IDeeColorConstants.DEE_BASICTYPES);
		}
		if(TokenUtil.isKeyword(tok) && isEnabled(IDeeColorConstants.DEE_KEYWORD)) {
			return getToken(IDeeColorConstants.DEE_KEYWORD);
		}
		if(TokenUtil.isOperator(tok) && isEnabled(IDeeColorConstants.DEE_OPERATORS)) {
			return getToken(IDeeColorConstants.DEE_OPERATORS);
		}
		if(isEnabled(IDeeColorConstants.DEE_DEFAULT)) {
			return getToken(IDeeColorConstants.DEE_DEFAULT);
		}
		
		return DEFAULT_TOKEN;
	}

	private boolean isEnabled(String key) {
		return LangColorPreferences.getIsEnabled(getPreferenceStore(), key);
	}

}
