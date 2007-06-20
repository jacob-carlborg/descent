package mmrnmhrm.ui.text.color;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.RGB;

public class LangColorPreferences {

	public static String getEnabledKey(String key) {
		return key + ILangColorPreferences.SUFFIX_ENABLE;
	}

	public static String getBoldKey(String key) {
		return key + ILangColorPreferences.SUFFIX_BOLD;
	}

	public static String getItalicKey(String key) {
		return key + ILangColorPreferences.SUFFIX_ITALIC;
	}

	public static String getUnderlineKey(String key) {
		return key + ILangColorPreferences.SUFFIX_UNDERLINE;
	}

	public static String getColorKey(String key) {
		return key + ILangColorPreferences.SUFFIX_COLOR;
	}


	public static boolean getIsEnabled(IPreferenceStore store, String key) {
		return store.getBoolean(getEnabledKey(key));
	}
	
	public static RGB getColor(IPreferenceStore store, String key) {
		return PreferenceConverter.getColor(store, getColorKey(key));
	}

	public static boolean getIsBold(IPreferenceStore store, String key) {
		return store.getBoolean(getBoldKey(key));
	}

	public static boolean getIsItalic(IPreferenceStore store, String key) {
		return store.getBoolean(getItalicKey(key));
	}
	
	public static boolean getIsUnderline(IPreferenceStore store, String key) {
		return store.getBoolean(getUnderlineKey(key));
	}
}
