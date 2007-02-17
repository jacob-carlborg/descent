package descent.ui.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import descent.internal.ui.JavaPlugin;

public class DMainPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	
	public DMainPreferencePage() {
		super(FieldEditorPreferencePage.GRID);
		
		IPreferenceStore store = JavaPlugin.getDefault().getPreferenceStore();
		setPreferenceStore(store);
	}
	
	public void init(IWorkbench workbench) {
	}

	@Override
	protected void createFieldEditors() {
	}
	
}
