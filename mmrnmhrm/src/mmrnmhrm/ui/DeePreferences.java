package mmrnmhrm.ui;

import org.eclipse.jface.preference.IPreferenceStore;

public abstract class DeePreferences {
	
	public static IPreferenceStore getPreferenceStore() {
		return DeePlugin.getInstance().getPreferenceStore();
	}

	public static void initializeDefaultValues(IPreferenceStore store) {
	}

}


