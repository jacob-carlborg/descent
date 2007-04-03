package mmrnmhrm.ui;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

/* TPLT */
public abstract class LangPreferences extends AbstractPreferenceInitializer{

	public static IPreferenceStore getPreferenceStore() {
		return DeePlugin.getInstance().getPreferenceStore();
	}

}