package descent.internal.ui.preferences;

import descent.internal.ui.IJavaHelpContextIds;
import descent.internal.ui.JavaPlugin;



/**
 * Occurrences preference page.
 * <p>
 * Note: Must be public since it is referenced from plugin.xml
 * </p>
 * 
 * @since 3.0
 */
public class OccurrencesPreferencePage extends AbstractConfigurationBlockPreferencePage {

	/*
	 * @see descent.internal.ui.preferences.AbstractConfigureationBlockPreferencePage#getHelpId()
	 */
	protected String getHelpId() {
		return IJavaHelpContextIds.JAVA_EDITOR_PREFERENCE_PAGE;
	}

	/*
	 * @see descent.internal.ui.preferences.AbstractConfigurationBlockPreferencePage#setDescription()
	 */
	protected void setDescription() {
		// This page has no description
	}
	
	/*
	 * @see descent.internal.ui.preferences.AbstractConfigurationBlockPreferencePage#setPreferenceStore()
	 */
	protected void setPreferenceStore() {
		setPreferenceStore(JavaPlugin.getDefault().getPreferenceStore());
	}

	/*
	 * @see descent.internal.ui.preferences.AbstractConfigureationBlockPreferencePage#createConfigurationBlock(descent.internal.ui.preferences.OverlayPreferenceStore)
	 */
	protected IPreferenceConfigurationBlock createConfigurationBlock(OverlayPreferenceStore overlayPreferenceStore) {
		return new MarkOccurrencesConfigurationBlock(overlayPreferenceStore);
	}
}
