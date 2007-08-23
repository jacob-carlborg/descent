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
		
		setIsEnabled(DeeColorConstants.DEE_DEFAULT, true);
		setColor(DeeColorConstants.DEE_DEFAULT, getRGB(SWT.COLOR_BLACK));
		setIsBold(DeeColorConstants.DEE_DEFAULT, false);
		setIsItalic(DeeColorConstants.DEE_DEFAULT, false);
		setIsUnderline(DeeColorConstants.DEE_DEFAULT, false);
		
		setIsEnabled(DeeColorConstants.DEE_KEYWORD, true);
		setColor(DeeColorConstants.DEE_KEYWORD, new RGB(0, 0, 127));
		setIsBold(DeeColorConstants.DEE_KEYWORD, true);
		setIsItalic(DeeColorConstants.DEE_KEYWORD, false);
		setIsUnderline(DeeColorConstants.DEE_KEYWORD, false);
		
		setIsEnabled(DeeColorConstants.DEE_BASICTYPES, true);
		setColor(DeeColorConstants.DEE_BASICTYPES, new RGB(0, 0, 127));
		setIsBold(DeeColorConstants.DEE_BASICTYPES, false);
		setIsItalic(DeeColorConstants.DEE_BASICTYPES, false);
		setIsUnderline(DeeColorConstants.DEE_BASICTYPES, false);
		
		setIsEnabled(DeeColorConstants.DEE_OPERATORS, true);
		setColor(DeeColorConstants.DEE_OPERATORS, getRGB(SWT.COLOR_BLACK));
		setIsBold(DeeColorConstants.DEE_OPERATORS, false);
		setIsItalic(DeeColorConstants.DEE_OPERATORS, false);
		setIsUnderline(DeeColorConstants.DEE_OPERATORS, false);

		setIsEnabled(DeeColorConstants.DEE_STRING, true);
		setColor(DeeColorConstants.DEE_STRING, getRGB(SWT.COLOR_DARK_YELLOW));
		setIsBold(DeeColorConstants.DEE_STRING, false);
		setIsItalic(DeeColorConstants.DEE_STRING, false);
		setIsUnderline(DeeColorConstants.DEE_STRING, false);

		setIsEnabled(DeeColorConstants.DEE_LITERALS, true);
		setColor(DeeColorConstants.DEE_LITERALS, new RGB(127, 64, 64));
		setIsBold(DeeColorConstants.DEE_LITERALS, false);
		setIsItalic(DeeColorConstants.DEE_LITERALS, false);
		setIsUnderline(DeeColorConstants.DEE_LITERALS, false);
		
		//RGB javaDocOthers = new RGB(63, 95, 191);
		//RGB javaDocTag = new RGB(63, 127, 95);
		setIsEnabled(DeeColorConstants.DEE_DOCCOMMENT, true);
		setColor(DeeColorConstants.DEE_DOCCOMMENT, new RGB(63, 95, 191));
		setIsBold(DeeColorConstants.DEE_DOCCOMMENT, false);
		setIsItalic(DeeColorConstants.DEE_DOCCOMMENT, false);
		setIsUnderline(DeeColorConstants.DEE_DOCCOMMENT, false);
		
		setIsEnabled(DeeColorConstants.DEE_COMMENT, true);
		setColor(DeeColorConstants.DEE_COMMENT, new RGB(63, 127, 95));
		setIsBold(DeeColorConstants.DEE_COMMENT, false);
		setIsItalic(DeeColorConstants.DEE_COMMENT, false);
		setIsUnderline(DeeColorConstants.DEE_COMMENT, false);


		setIsEnabled(DeeColorConstants.DEE_SPECIAL, false);
		setColor(DeeColorConstants.DEE_SPECIAL, getRGB(SWT.COLOR_CYAN));
		setIsBold(DeeColorConstants.DEE_SPECIAL, false);
		setIsItalic(DeeColorConstants.DEE_SPECIAL, false);
		setIsUnderline(DeeColorConstants.DEE_SPECIAL, true);
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
