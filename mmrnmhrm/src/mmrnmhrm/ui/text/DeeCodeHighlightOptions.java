package mmrnmhrm.ui.text;

import java.util.HashMap;
import java.util.Map;

import mmrnmhrm.ui.preferences.ColorConstants;
import mmrnmhrm.ui.preferences.ColorManager;

import org.eclipse.jdt.ui.text.IColorManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

import descent.internal.core.dom.Lexer;
import descent.internal.core.dom.TOK;

public class DeeCodeHighlightOptions 
{
	private static DeeCodeHighlightOptions defaultInstance;
	
	static {
		defaultInstance = new DeeCodeHighlightOptions();
	}

	public static DeeCodeHighlightOptions getDefault() {
		return defaultInstance;
	}
	
	
	private ColorRegistry colorManager;
	private IPreferenceStore preferenceStore;

	private Map<String, Token> tokenMap= new HashMap<String, Token>();
	

    public DeeCodeHighlightOptions()
    {
    	colorManager = new ColorRegistry();
    	loadColors();
    	initTokens(); 
    }
	private void loadColors() {
		//preferenceStore.contains("sdf");
    	colorManager.put(IDeeColorConstants.DEE_DEFAULT, new RGB(0,0,0));
    	colorManager.put(IDeeColorConstants.DEE_KEYWORD, ColorConstants.XXXBLUE);
    	colorManager.put(IDeeColorConstants.DEE_COMMENT, ColorConstants.XXXGREEN);
    	colorManager.put(IDeeColorConstants.DEE_STRING, ColorConstants.XXXGRAY);
	}

	private void initTokens() {
    	loadToken(IDeeColorConstants.DEE_DEFAULT);
    	loadToken(IDeeColorConstants.DEE_KEYWORD);
    	loadToken(IDeeColorConstants.DEE_COMMENT);
    	loadToken(IDeeColorConstants.DEE_STRING);
	}

	private void loadToken(String key) {
		Color color = colorManager.get(key);
		TextAttribute textAttribute = new TextAttribute(color);
		Token token = new Token(textAttribute);
        tokenMap.put(key, token);
	}

	public IToken getAttributes(TOK tok) {
		if( Lexer.keywords.containsValue(tok))
			return tokenMap.get(IDeeColorConstants.DEE_KEYWORD);
		else if(tok == TOK.TOKstring)
			return tokenMap.get(IDeeColorConstants.DEE_STRING);
		else if(tok == TOK.TOKcomment)
			return tokenMap.get(IDeeColorConstants.DEE_COMMENT);
		else
			return tokenMap.get(IDeeColorConstants.DEE_DEFAULT);
	}

}
