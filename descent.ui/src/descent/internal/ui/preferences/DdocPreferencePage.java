package descent.internal.ui.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import descent.internal.ui.JavaPlugin;
import descent.ui.PreferenceConstants;

public class DdocPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public DdocPreferencePage() {
		super(GRID);
		
		setPreferenceStore(JavaPlugin.getDefault().getPreferenceStore());
		setDescription(PreferencesMessages.DdocPreferencePage_description); 
	}

	@Override
	protected void createFieldEditors() {
		Composite parent = getFieldEditorParent();
		
		addField(new BooleanFieldEditor(PreferenceConstants.DDOC_SHOW_PARAMETER_TYPES, PreferencesMessages.DdocPreferencePage_show_parameter_types, parent));
	}

	public void init(IWorkbench workbench) {
	}

	

}
