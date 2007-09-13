package mmrnmhrm.ui;

import mmrnmhrm.ui.text.color.DeeColorPreferenceInitializer;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.dltk.ui.PreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.editors.text.EditorsUI;

public class DeeUIPreferenceInitializer extends AbstractPreferenceInitializer {
	
	// Extension point entry point
	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = DeePlugin.getInstance().getPreferenceStore();

		initializeDefaultValues(store);
	}

	private void initializeDefaultValues(IPreferenceStore store) {
		EditorsUI.useAnnotationsPreferencePage(store);
		EditorsUI.useQuickDiffPreferencePage(store);
		
		PreferenceConstants.initializeDefaultValues(store);
	
		DeeColorPreferenceInitializer.initializeDefaults(store);
		
		store.setDefault(PreferenceConstants.CODEASSIST_AUTOACTIVATION_TRIGGERS, ".");
		store.setDefault(PreferenceConstants.CODEASSIST_AUTOINSERT, true);
		store.setDefault(PreferenceConstants.CODEASSIST_PREFIX_COMPLETION, true);
		
		store.setDefault( PreferenceConstants.EDITOR_SMART_INDENT, true);
		//store.setDefault( PreferenceConstants.EDITOR_TAB_ALWAYS_INDENT, false);
		store.setDefault( PreferenceConstants.EDITOR_CLOSE_STRINGS, true);
		store.setDefault( PreferenceConstants.EDITOR_CLOSE_BRACKETS, true);
		store.setDefault( PreferenceConstants.EDITOR_CLOSE_BRACES, true);
		store.setDefault( PreferenceConstants.EDITOR_SMART_TAB, true);
		store.setDefault( PreferenceConstants.EDITOR_SMART_PASTE, true);
		store.setDefault( PreferenceConstants.EDITOR_SMART_HOME_END, true);
		store.setDefault( PreferenceConstants.EDITOR_SUB_WORD_NAVIGATION, true);		
		store.setDefault( PreferenceConstants.EDITOR_TAB_WIDTH, 4);
		store.setDefault( PreferenceConstants.EDITOR_SYNC_OUTLINE_ON_CURSOR_MOVE, true);
		
		// folding
		store.setDefault(PreferenceConstants.EDITOR_FOLDING_ENABLED, true);
		store.setDefault(PreferenceConstants.EDITOR_COMMENTS_FOLDING_ENABLED, true);		
		

		// WIZARDS
		store.setDefault(PreferenceConstants.SRCBIN_FOLDERS_IN_NEWPROJ, true);
		store.setDefault(PreferenceConstants.SRC_SRCNAME, "src"); //$NON-NLS-1$		

	}
	
}



