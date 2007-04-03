package mmrnmhrm.ui;

import mmrnmhrm.ui.text.color.DeeColorPreferenceInitializer;

import org.eclipse.jface.preference.IPreferenceStore;

public class DeePreferences extends LangPreferences {
	

	// Extension point entry point
	public void initializeDefaultPreferences() {
		IPreferenceStore store = DeePreferences.getPreferenceStore();

		DeeColorPreferenceInitializer.initializeDefaults(store);
	}
}



