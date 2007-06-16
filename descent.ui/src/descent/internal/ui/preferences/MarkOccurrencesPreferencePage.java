package descent.internal.ui.preferences;


import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import descent.internal.ui.IJavaHelpContextIds;
import descent.internal.ui.JavaPlugin;

/**
 * The page for setting the editor options.
 */
public final class MarkOccurrencesPreferencePage extends AbstractConfigurationBlockPreferencePage {
	
	/*
	 * @see org.eclipse.ui.internal.editors.text.AbstractConfigureationBlockPreferencePage#getHelpId()
	 */
	protected String getHelpId() {
		return IJavaHelpContextIds.JAVA_EDITOR_PREFERENCE_PAGE;
	}

	/*
	 * @see org.eclipse.ui.internal.editors.text.AbstractConfigurationBlockPreferencePage#setDescription()
	 */
	protected void setDescription() {
		String description= PreferencesMessages.MarkOccurrencesConfigurationBlock_title; 
		setDescription(description);
	}
	
	/*
	 * @see org.org.eclipse.ui.internal.editors.text.AbstractConfigurationBlockPreferencePage#setPreferenceStore()
	 */
	protected void setPreferenceStore() {
		setPreferenceStore(JavaPlugin.getDefault().getPreferenceStore());
	}
	
	
	protected Label createDescriptionLabel(Composite parent) {
		return null; // no description for new look.
	}

	/*
	 * @see org.eclipse.ui.internal.editors.text.AbstractConfigureationBlockPreferencePage#createConfigurationBlock(org.eclipse.ui.internal.editors.text.OverlayPreferenceStore)
	 */
	protected IPreferenceConfigurationBlock createConfigurationBlock(OverlayPreferenceStore overlayPreferenceStore) {
		return new MarkOccurrencesConfigurationBlock(overlayPreferenceStore);
	}

}
