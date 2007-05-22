package mmrnmhrm.ui.preferences;

import melnorme.util.ui.RowComposite;
import mmrnmhrm.org.eclipse.ui.internal.editors.text.OverlayPreferenceStore;
import mmrnmhrm.ui.DeePlugin;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;


/**
 * The root/base preference page for Dee 
 */
public class DeeBasePreferencePage extends AbstractPreferencePage {

	public DeeBasePreferencePage() {
		super("Base preference page");
		setDescription("D base preference page.");
		fOverlayPrefStore = new OverlayPreferenceStore(
			DeePlugin.getPrefStore(), 
			new OverlayPreferenceStore.OverlayKey[] {});

	}
	
	@Override
	protected Control createContents(Composite parent) {
		return new RowComposite(parent);
	}

}
