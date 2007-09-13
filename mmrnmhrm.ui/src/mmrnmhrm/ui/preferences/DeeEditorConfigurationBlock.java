package mmrnmhrm.ui.preferences;

import org.eclipse.dltk.ui.preferences.EditorConfigurationBlock;
import org.eclipse.dltk.ui.preferences.OverlayPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class DeeEditorConfigurationBlock extends EditorConfigurationBlock {

	public DeeEditorConfigurationBlock(PreferencePage mainPreferencePage,
			OverlayPreferenceStore store, boolean disableSmart,
			boolean tabAlwaysIndent) {
		super(mainPreferencePage, store, disableSmart, tabAlwaysIndent);
	}
	
	@Override
	public Control createControl(Composite parent) {
		initializeDialogUnits(parent);

		Composite control = new Composite(parent, SWT.NONE);
		control.setLayout(new GridLayout());
		
		/*if( !smartDisabled ) {
			Composite composite;

			composite = createSubsection(control,null, PreferencesMessages.EditorPreferencePage_title0);
			createSettingsGroup(composite);
		}
		
		createTabsGroup(control);
		*/
		return control;
	}

}
