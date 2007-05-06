package descent.internal.launching.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import descent.launching.DescentLaunching;
import descent.launching.IDescentLaunchingPreferenceConstants;

public class DescentDebugPreferencesInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore prefs = DescentLaunching.getDefault().getPreferenceStore();
		
		prefs.setDefault(IDescentLaunchingPreferenceConstants.DEBUGGER_TIMEOUT, 3000);
		prefs.setDefault(IDescentLaunchingPreferenceConstants.SHOW_BASE_MEMBERS_IN_SAME_LEVEL, false);
	}

}
