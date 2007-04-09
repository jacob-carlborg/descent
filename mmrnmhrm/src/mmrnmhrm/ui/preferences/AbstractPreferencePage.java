package mmrnmhrm.ui.preferences;

import mmrnmhrm.org.eclipse.ui.internal.editors.text.OverlayPreferenceStore;
import mmrnmhrm.ui.ActualPlugin;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Common preference page class. Manages an OverlayPreferenceStore. 
 */
public abstract class AbstractPreferencePage extends PreferencePage implements IWorkbenchPreferencePage{

	/** Preference store used to hold temporary values, until the 
	 * user saves the preference page. */
	protected OverlayPreferenceStore fOverlayPrefStore;

	public AbstractPreferencePage(String title) {
		super(title);
		setPreferenceStore(getPreferenceStore());
		fOverlayPrefStore= new OverlayPreferenceStore(
				getPreferenceStore(), 
				new OverlayPreferenceStore.OverlayKey[] {});
	}
	
	/** {@inheritDoc} */
	public void init(IWorkbench workbench) {
		// Nothing to do
	}
	
	/** Gets the preference store for this page. */
	public IPreferenceStore getPreferenceStore() {
		// return the original one?
		return ActualPlugin.getPrefStore();
	}
	
	@Override
	public boolean performOk() {
		fOverlayPrefStore.propagate();
		ActualPlugin.getInstance().savePluginPreferences();
		return true;
	}

	@Override
	public void performDefaults() {
		fOverlayPrefStore.loadDefaults();
		super.performDefaults();
	}
	
	@Override
	public void dispose() {
		if (fOverlayPrefStore != null) {
			fOverlayPrefStore.stop();
			fOverlayPrefStore= null;
		}
		super.dispose();
	}


}