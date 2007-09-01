package mmrnmhrm.core;



import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

public class DeeCorePreferences  {
	
	public static final String PREFIX = DeeCore.PLUGIN_ID + "."; 
	
	public static final String ADAPT_MALFORMED_DMD_AST = "adapt_malformed_ast";

	public static void initializeDefaultPreferences(IEclipsePreferences prefs) {
		prefs.putBoolean(ADAPT_MALFORMED_DMD_AST, true); 
	}

	public static void initializeDefaultPreferences(Preferences pluginPrefs) {
		pluginPrefs.setDefault(ADAPT_MALFORMED_DMD_AST, true); 
	}
	
	public static boolean getBoolean(String key) {
		return DeeCore.getInstance().getPluginPreferences().getBoolean(key);
	}

	public static void setBoolean(String key, boolean val) {
		DeeCore.getInstance().getPluginPreferences().setValue(key, val);
	}
	
}