package mmrnmhrm.ui.text.color;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public abstract class DeeColorPreferenceInitializer {
	
	private static IPreferenceStore store;


	private static RGB getRGB(int colorid) {
		// http://blog.platinumsolutions.com/node/155
		return Display.getDefault().getSystemColor(colorid).getRGB();
	}
	
	/** Sets the defaults for the color preferences. */ 
	public static void initializeDefaults(IPreferenceStore store) {
		DeeColorPreferenceInitializer.store = store;
		
		setIsEnabled(IDeeColorConstants.DEE_DEFAULT, true);
		setColor(IDeeColorConstants.DEE_DEFAULT, getRGB(SWT.COLOR_BLACK));
		setIsBold(IDeeColorConstants.DEE_DEFAULT, false);
		setIsItalic(IDeeColorConstants.DEE_DEFAULT, false);
		setIsUnderline(IDeeColorConstants.DEE_DEFAULT, false);
		
		setIsEnabled(IDeeColorConstants.DEE_KEYWORD, true);
		setColor(IDeeColorConstants.DEE_KEYWORD, new RGB(0, 0, 127));
		setIsBold(IDeeColorConstants.DEE_KEYWORD, true);
		setIsItalic(IDeeColorConstants.DEE_KEYWORD, false);
		setIsUnderline(IDeeColorConstants.DEE_KEYWORD, false);
		
		setIsEnabled(IDeeColorConstants.DEE_BASICTYPES, true);
		setColor(IDeeColorConstants.DEE_BASICTYPES, new RGB(0, 0, 127));
		setIsBold(IDeeColorConstants.DEE_BASICTYPES, false);
		setIsItalic(IDeeColorConstants.DEE_BASICTYPES, false);
		setIsUnderline(IDeeColorConstants.DEE_BASICTYPES, false);
		
		setIsEnabled(IDeeColorConstants.DEE_OPERATORS, true);
		setColor(IDeeColorConstants.DEE_OPERATORS, getRGB(SWT.COLOR_BLACK));
		setIsBold(IDeeColorConstants.DEE_OPERATORS, false);
		setIsItalic(IDeeColorConstants.DEE_OPERATORS, false);
		setIsUnderline(IDeeColorConstants.DEE_OPERATORS, false);

		setIsEnabled(IDeeColorConstants.DEE_STRING, true);
		setColor(IDeeColorConstants.DEE_STRING, getRGB(SWT.COLOR_DARK_YELLOW));
		setIsBold(IDeeColorConstants.DEE_STRING, false);
		setIsItalic(IDeeColorConstants.DEE_STRING, false);
		setIsUnderline(IDeeColorConstants.DEE_STRING, false);

		setIsEnabled(IDeeColorConstants.DEE_LITERALS, true);
		setColor(IDeeColorConstants.DEE_LITERALS, new RGB(127, 64, 64));
		setIsBold(IDeeColorConstants.DEE_LITERALS, false);
		setIsItalic(IDeeColorConstants.DEE_LITERALS, false);
		setIsUnderline(IDeeColorConstants.DEE_LITERALS, false);
		
		//RGB javaDocOthers = new RGB(63, 95, 191);
		//RGB javaDocTag = new RGB(63, 127, 95);
		setIsEnabled(IDeeColorConstants.DEE_DOCCOMMENT, true);
		setColor(IDeeColorConstants.DEE_DOCCOMMENT, new RGB(63, 95, 191));
		setIsBold(IDeeColorConstants.DEE_DOCCOMMENT, false);
		setIsItalic(IDeeColorConstants.DEE_DOCCOMMENT, false);
		setIsUnderline(IDeeColorConstants.DEE_DOCCOMMENT, false);
		
		setIsEnabled(IDeeColorConstants.DEE_COMMENT, true);
		setColor(IDeeColorConstants.DEE_COMMENT, new RGB(63, 127, 95));
		setIsBold(IDeeColorConstants.DEE_COMMENT, false);
		setIsItalic(IDeeColorConstants.DEE_COMMENT, false);
		setIsUnderline(IDeeColorConstants.DEE_COMMENT, false);


		setIsEnabled(IDeeColorConstants.DEE_SPECIAL, false);
		setColor(IDeeColorConstants.DEE_SPECIAL, getRGB(SWT.COLOR_CYAN));
		setIsBold(IDeeColorConstants.DEE_SPECIAL, false);
		setIsItalic(IDeeColorConstants.DEE_SPECIAL, false);
		setIsUnderline(IDeeColorConstants.DEE_SPECIAL, true);
	}


	private static void setIsEnabled(String key, boolean enabled) {
		store.setDefault(LangColorPreferences.getEnabledKey(key), enabled);
	}
	
	private static void setColor(String key, RGB rgb) {
		PreferenceConverter.setDefault(store, LangColorPreferences.getColorKey(key), rgb);
	}

	private static void setIsBold(String key, boolean bold) {
		store.setDefault(LangColorPreferences.getBoldKey(key), bold);
	}
	
	private static void setIsItalic( String key, boolean italic) {
		store.setDefault(LangColorPreferences.getItalicKey(key), italic);
	}
		
	private static void setIsUnderline(String key, boolean underline) {
		store.setDefault(LangColorPreferences.getUnderlineKey(key), underline);
	}

}
