package mmrnmhrm.ui;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

public class DeeUIPreferenceInitializer extends AbstractPreferenceInitializer {


	public void initializeDefaultPreferences() {
		IPreferenceStore store = DeePreferences.getPreferenceStore();
		DeePreferences.initializeDefaultValues(store);
	}

}
