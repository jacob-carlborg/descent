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

public class DMainPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	
	public DMainPreferencePage() {
		super(FieldEditorPreferencePage.GRID);
		
		IPreferenceStore store = DescentUI.getDefault().getPreferenceStore();
		setPreferenceStore(store);
	}
	
	public void init(IWorkbench workbench) {
	}

	@Override
	protected void createFieldEditors() {
	}
	
}
