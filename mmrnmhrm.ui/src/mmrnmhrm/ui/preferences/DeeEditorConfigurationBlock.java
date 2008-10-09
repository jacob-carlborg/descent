package mmrnmhrm.ui.preferences;

import org.eclipse.dltk.ui.preferences.EditorConfigurationBlock;
import org.eclipse.dltk.ui.preferences.OverlayPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
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
		return super.createControl(parent);
	}

}
