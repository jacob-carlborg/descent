package mmrnmhrm.ui.preferences;

import melnorme.util.ui.swt.RowComposite;
import mmrnmhrm.ui.ActualPlugin;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;


/**
 * The root/base preference page for Dee 
 */
public class DeeRootPreferencePage extends PreferencePage implements IWorkbenchPreferencePage{

	public DeeRootPreferencePage() {
		super("Base preference page");
		setDescription("D root preference page.");
	}
	
	/** {@inheritDoc} */
	public void init(IWorkbench workbench) {
		// Nothing to do
	}
	
	@Override
	protected Control createContents(Composite parent) {
		return new RowComposite(parent);
	}
	
	/** Gets the preference store for this page. */
	public IPreferenceStore getPreferenceStore() {
		return ActualPlugin.getPrefStore();
	}
	
}
