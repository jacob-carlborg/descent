package mmrnmhrm.ui.preferences.pages;

import melnorme.util.ui.swt.RowComposite;
import mmrnmhrm.core.DeeCorePreferences;
import mmrnmhrm.ui.ActualPlugin;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;


/**
 * The root/base preference page for Dee 
 */
public class DeeRootPreferencePage extends PreferencePage implements IWorkbenchPreferencePage{

	private Button fAdaptedMalformedAST;
	
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
		RowComposite content = new RowComposite(parent);
		
		fAdaptedMalformedAST = new Button(content, SWT.CHECK);
		fAdaptedMalformedAST.setText("Adapt source AST when there are syntax errors.");
		/*fAdaptedMalformedAST.addSelectionListener(new SimpleSelectionListener() {
			public void widgetSelected(SelectionEvent e) {
			}
		});*/
		/*
		gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gd.horizontalIndent = 20;
		gd.horizontalSpan = 2;
		fStrikethroughCheckBox.setLayoutData(gd);
		*/
		performDefaults();
		return content;
	}
	
	/** Gets the preference store for this page. */
	public IPreferenceStore getPreferenceStore() {
		return ActualPlugin.getPrefStore();
	}
	
	@Override
	protected void performDefaults() {
		fAdaptedMalformedAST.setSelection(DeeCorePreferences.getBoolean(
				DeeCorePreferences.ADAPT_MALFORMED_DMD_AST));
		super.performDefaults();
	}
	
	@Override
	public boolean performOk() {
		DeeCorePreferences.setBoolean(DeeCorePreferences.ADAPT_MALFORMED_DMD_AST, 
				fAdaptedMalformedAST.getSelection());
		return super.performOk();
	}
	
}
