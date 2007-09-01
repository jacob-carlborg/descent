package mmrnmhrm.core;



import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;

public class DeeCorePreferenceInitializer extends AbstractPreferenceInitializer {

	public DeeCorePreferenceInitializer() {
	}

	@Override
	public void initializeDefaultPreferences() {
	 	//IEclipsePreferences preferences = (new DefaultScope()).getNode(DeeCore.PLUGIN_ID);
	 	//DeeCorePreferences.initializeDefaultPreferences(preferences);
		Preferences pluginPreferences = DeeCore.getInstance().getPluginPreferences();
		DeeCorePreferences.initializeDefaultPreferences(pluginPreferences);
	}
	
/*	private static IPreferenceStore preferenceStore = null;

	public static IPreferenceStore getPreferenceStore() {
        // Create the preference store lazily.
        if (preferenceStore == null) {
            preferenceStore = new ScopedPreferenceStore(new InstanceScope(), DeeCore.getInstance().getBundle().getSymbolicName());

        }
        return preferenceStore;
    }
*/
	
}
