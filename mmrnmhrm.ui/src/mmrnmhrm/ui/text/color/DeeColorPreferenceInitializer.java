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
		
		setIsEnabled(IDeeColorPreferences.DEE_DEFAULT, true);
		setColor(IDeeColorPreferences.DEE_DEFAULT, getRGB(SWT.COLOR_BLACK));
		setIsBold(IDeeColorPreferences.DEE_DEFAULT, false);
		setIsItalic(IDeeColorPreferences.DEE_DEFAULT, false);
		setIsUnderline(IDeeColorPreferences.DEE_DEFAULT, false);
		
		setIsEnabled(IDeeColorPreferences.DEE_KEYWORD, true);
		setColor(IDeeColorPreferences.DEE_KEYWORD, new RGB(0, 0, 127));
		setIsBold(IDeeColorPreferences.DEE_KEYWORD, true);
		setIsItalic(IDeeColorPreferences.DEE_KEYWORD, false);
		setIsUnderline(IDeeColorPreferences.DEE_KEYWORD, false);
		
		setIsEnabled(IDeeColorPreferences.DEE_BASICTYPES, true);
		setColor(IDeeColorPreferences.DEE_BASICTYPES, new RGB(0, 0, 127));
		setIsBold(IDeeColorPreferences.DEE_BASICTYPES, false);
		setIsItalic(IDeeColorPreferences.DEE_BASICTYPES, false);
		setIsUnderline(IDeeColorPreferences.DEE_BASICTYPES, false);
		
		setIsEnabled(IDeeColorPreferences.DEE_OPERATORS, true);
		setColor(IDeeColorPreferences.DEE_OPERATORS, getRGB(SWT.COLOR_DARK_RED));
		setIsBold(IDeeColorPreferences.DEE_OPERATORS, true);
		setIsItalic(IDeeColorPreferences.DEE_OPERATORS, false);
		setIsUnderline(IDeeColorPreferences.DEE_OPERATORS, false);

		setIsEnabled(IDeeColorPreferences.DEE_STRING, true);
		setColor(IDeeColorPreferences.DEE_STRING, getRGB(SWT.COLOR_DARK_YELLOW));
		setIsBold(IDeeColorPreferences.DEE_STRING, false);
		setIsItalic(IDeeColorPreferences.DEE_STRING, false);
		setIsUnderline(IDeeColorPreferences.DEE_STRING, false);

		setIsEnabled(IDeeColorPreferences.DEE_LITERALS, true);
		setColor(IDeeColorPreferences.DEE_LITERALS, new RGB(127, 64, 64));
		setIsBold(IDeeColorPreferences.DEE_LITERALS, true);
		setIsItalic(IDeeColorPreferences.DEE_LITERALS, false);
		setIsUnderline(IDeeColorPreferences.DEE_LITERALS, false);

		
		//RGB javaDocOthers = new RGB(63, 95, 191);
		//RGB javaDocTag = new RGB(63, 127, 95);
		setIsEnabled(IDeeColorPreferences.DEE_DOCCOMMENT, true);
		setColor(IDeeColorPreferences.DEE_DOCCOMMENT, new RGB(63, 95, 191));
		setIsBold(IDeeColorPreferences.DEE_DOCCOMMENT, false);
		setIsItalic(IDeeColorPreferences.DEE_DOCCOMMENT, false);
		setIsUnderline(IDeeColorPreferences.DEE_DOCCOMMENT, false);
		
		setIsEnabled(IDeeColorPreferences.DEE_COMMENT, true);
		setColor(IDeeColorPreferences.DEE_COMMENT, new RGB(63, 127, 95));
		setIsBold(IDeeColorPreferences.DEE_COMMENT, false);
		setIsItalic(IDeeColorPreferences.DEE_COMMENT, false);
		setIsUnderline(IDeeColorPreferences.DEE_COMMENT, false);


		setIsEnabled(IDeeColorPreferences.DEE_SPECIAL, false);
		setColor(IDeeColorPreferences.DEE_SPECIAL, getRGB(SWT.COLOR_CYAN));
		setIsBold(IDeeColorPreferences.DEE_SPECIAL, false);
		setIsItalic(IDeeColorPreferences.DEE_SPECIAL, false);
		setIsUnderline(IDeeColorPreferences.DEE_SPECIAL, true);
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
