package descent.ui.preferences;

import java.io.File;

import org.eclipse.jface.preference.*;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;

import descent.core.JavaCore;
import descent.internal.ui.JavaPlugin;

/**
 * This class represents a preference page that
 * is contributed to the Preferences dialog. By 
 * subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows
 * us to create a page that is small and knows how to 
 * save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They
 * are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can
 * be accessed directly via the preference store.
 */

public class DCompilerSettings
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	DirectoryFieldEditor compilerLocation;
	RadioGroupFieldEditor compilerType;
	public DCompilerSettings() {
		super(GRID);
		setPreferenceStore(JavaCore.getPlugin().getPreferenceStore());
		setDescription("Location of D Compiler");
	}
	
	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	public void createFieldEditors() {
		
		compilerLocation = new DirectoryFieldEditor("descent.ui.preferences.DCompilerSettings.compilerLocation", 
				"&Compiler Location:", getFieldEditorParent());
		addField(compilerLocation);
		
		compilerType = new RadioGroupFieldEditor(
				"descent.ui.preferences.DCompilerSettings.compilerType",
				"Compiler Type:",
				1,
				new String[][] { { "&DMD", "dmd" }, {
					"&GDC", "gdc" }
			}, getFieldEditorParent());
		
		addField(compilerType);
		
	}
	@Override
	public void checkState()
	{
		
		
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}
	
}