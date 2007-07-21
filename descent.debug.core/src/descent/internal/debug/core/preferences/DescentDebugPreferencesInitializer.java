package descent.internal.debug.core.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import descent.debug.core.DescentDebugPlugin;
import descent.debug.core.IDescentLaunchingPreferenceConstants;

public class DescentDebugPreferencesInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore prefs = DescentDebugPlugin.getDefault().getPreferenceStore();
		
		prefs.setDefault(IDescentLaunchingPreferenceConstants.DEBUGGER_TIMEOUT, 3000);
		prefs.setDefault(IDescentLaunchingPreferenceConstants.SHOW_BASE_MEMBERS_IN_SAME_LEVEL, false);
	}

}
