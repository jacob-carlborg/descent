package descent.ui.preferences;

import java.io.File;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import descent.ui.DescentUI;

public class DCompilerPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	
	private DirectoryFieldEditor dirField;
	
	public DCompilerPreferencePage() {
		super(FieldEditorPreferencePage.GRID);
		
		IPreferenceStore store = DescentUI.getDefault().getPreferenceStore();
		setPreferenceStore(store);
	}
	
	public void init(IWorkbench workbench) {
	}

	@Override
	protected void createFieldEditors() {
		Composite parent = getFieldEditorParent();

		dirField = new DirectoryFieldEditor(DescentUI.PREFERENCE_D_ROOT, "D root:", parent);
		dirField.getTextControl(parent).addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				validate();
			}
		});
		addField(dirField);
		
		validate();
	}
	
	private void validate() {
		File file = new File(dirField.getTextControl(getFieldEditorParent()).getText());
		if (!file.exists()) {
			setErrorMessage("The path dosen't exist");
			setValid(false);
			return;
		}
		
		setErrorMessage(null);
		setValid(true);
	}

}
