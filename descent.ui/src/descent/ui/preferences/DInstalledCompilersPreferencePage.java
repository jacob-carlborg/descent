package descent.ui.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

public class DInstalledCompilersPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	private DInstalledCompilerBlock fPrefsBlock;
	
	public DInstalledCompilersPreferencePage() {
		setDescription("Installed Compilers"); //TODO externalize String
		noDefaultAndApplyButton();
	}
	
	public void init(IWorkbench workbench) {
	}

	
	@Override
	protected Control createContents(Composite parent) {
		Composite topPane = new Composite( parent, SWT.NONE );
		
		topPane.setLayout(new GridLayout());
		topPane.setLayoutData(new GridData( GridData.FILL_BOTH ));
		
		fPrefsBlock = new DInstalledCompilerBlock( null );
		
		//PlatformUI.getWorkbench().getHelpSystem().setHelp(topPane, 0 );
		return fPrefsBlock.createControl( topPane );
	}
	

}
