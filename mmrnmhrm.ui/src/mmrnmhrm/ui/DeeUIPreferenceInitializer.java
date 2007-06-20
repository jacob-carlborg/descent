package mmrnmhrm.ui;

import mmrnmhrm.ui.text.color.DeeColorPreferenceInitializer;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

public class DeeUIPreferenceInitializer extends AbstractPreferenceInitializer {
	

	// Extension point entry point
	public void initializeDefaultPreferences() {
		IPreferenceStore store = getPreferenceStore();

		DeeColorPreferenceInitializer.initializeDefaults(store);
	}
	
	public static IPreferenceStore getPreferenceStore() {
		return DeePlugin.getInstance().getPreferenceStore();
	}
}



