package descent.internal.launching.ui.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import descent.internal.launching.ui.util.ComboFieldEditor;
import descent.launching.DescentLaunching;
import descent.launching.IDebuggerDescriptor;
import descent.launching.IDescentLaunchingPreferenceConstants;

public class DescentDebugPreferencesPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	
	public DescentDebugPreferencesPage() {
		super(GRID);
		
		IPreferenceStore store= DescentLaunching.getDefault().getPreferenceStore();
		setPreferenceStore(store);
		setDescription("General settings for D debugging"); 
	}

	@Override
	protected void createFieldEditors() {
		IDebuggerDescriptor[] debuggers = DescentLaunching.getDebuggerRegistry().getDebuggers();
		String[][] comboValues = new String[debuggers.length][];
		for(int i = 0; i < debuggers.length; i++) {
			comboValues[i] = new String[] { debuggers[i].getName(), debuggers[i].getId() };
		}
		addField(new ComboFieldEditor(IDescentLaunchingPreferenceConstants.DEBUGGER_ID, "Debugger:", comboValues, getFieldEditorParent()));
		
		addField(new FileFieldEditor(IDescentLaunchingPreferenceConstants.DEBUGGER_PATH, "Debugger executable:", getFieldEditorParent()));
		 
		IntegerFieldEditor timeout = new IntegerFieldEditor(IDescentLaunchingPreferenceConstants.DEBUGGER_TIMEOUT, "Debugger timeout (ms):", getFieldEditorParent());
		timeout.setValidRange(0, Integer.MAX_VALUE);
		addField(timeout);
		 
		addField(new BooleanFieldEditor(IDescentLaunchingPreferenceConstants.SHOW_BASE_MEMBERS_IN_SAME_LEVEL, "Show base members in the same level as the parent class", getFieldEditorParent()));
	}

	public void init(IWorkbench workbench) {
	}

}
