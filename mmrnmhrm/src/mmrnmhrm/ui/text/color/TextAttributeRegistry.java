package mmrnmhrm.ui.text.color;

import java.util.HashMap;
import java.util.Map;

import mmrnmhrm.ui.ActualPlugin;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

/**
 * Maintains a mapping between color preference keys and their respective
 * Token (textual attribute).
 */
public class TextAttributeRegistry 
{
	public static Token DEFAULT_TOKEN = new Token(new TextAttribute(null));

	private static TextAttributeRegistry instance = new TextAttributeRegistry();
	
	/** Return a default TextAttributeRegistry instance. */
	public static TextAttributeRegistry getDefault() {
		return instance;
	}
	
	
	private ColorRegistry colorManager;
	private IPreferenceStore prefStore;

	private Map<String, Token> tokenMap= new HashMap<String, Token>();
	
	/** Creates a new TextAttrubuteRegistry. */
    public TextAttributeRegistry()
    {
    	colorManager = new ColorRegistry();
    	prefStore = ActualPlugin.getInstance().getPreferenceStore(); 
    }
	
	/** Creates a Token (textual attribute) for the given color preference key,
	 * and stores it in the manager. */
	public void loadToken(String key) {
		if(LangColorPreferences.getIsEnabled(prefStore, key) == false) {
			tokenMap.put(key, null);
			return;
		}

		Color color = loadColor(key);
		int style = 0;
		if(LangColorPreferences.getIsBold(prefStore, key))
			style |= SWT.BOLD;
		if(LangColorPreferences.getIsItalic(prefStore, key))
			style |= SWT.ITALIC;
		if(LangColorPreferences.getIsUnderline(prefStore, key))
			style |= TextAttribute.UNDERLINE;

		TextAttribute textAttribute = new TextAttribute(color, null, style);
		Token token = new Token(textAttribute);
        tokenMap.put(key, token);
	}
	
	private Color loadColor(String key) {
		Color color = colorManager.get(key);
		if(color == null) { 
			RGB rgb = LangColorPreferences.getColor(prefStore, key);
			colorManager.put(key, rgb);
			color = colorManager.get(key);
		}
		return color;
	}
	
	/** Gets the Token (textual attribute) for the given color preference key. */
	public IToken getToken(String key) {
		return tokenMap.get(key);
	}
	
}
