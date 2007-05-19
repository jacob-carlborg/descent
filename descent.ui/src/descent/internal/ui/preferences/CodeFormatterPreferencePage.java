package descent.internal.ui.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

import descent.internal.ui.preferences.ProfilePreferencePage;
import descent.internal.ui.preferences.PreferencesAccess;
import descent.internal.ui.preferences.PreferencesMessages;
import descent.internal.ui.IJavaHelpContextIds;
import descent.internal.ui.preferences.formatter.CodeFormatterConfigurationBlock;
import descent.internal.ui.preferences.formatter.ProfileConfigurationBlock;

public class CodeFormatterPreferencePage extends ProfilePreferencePage {

	public static final String PREF_ID= "descent.ui.preferences.CodeFormatterPreferencePage"; //$NON-NLS-1$
	public static final String PROP_ID= "descent.ui.propertyPages.CodeFormatterPreferencePage"; //$NON-NLS-1$
	
	public CodeFormatterPreferencePage() {		
		// only used when page is shown programatically
		setTitle(PreferencesMessages.CodeFormatterPreferencePage_title);		 
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.preferences.ProfilePreferencePage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
	    super.createControl(parent);
    	PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(), IJavaHelpContextIds.CODEFORMATTER_PREFERENCE_PAGE);
	}

	protected ProfileConfigurationBlock createConfigurationBlock(PreferencesAccess access) {
	    return new CodeFormatterConfigurationBlock(getProject(), access);
    }

	/* (non-Javadoc)
	 * @see descent.internal.ui.preferences.PropertyAndPreferencePage#getPreferencePageID()
	 */
	protected String getPreferencePageID() {
		return PREF_ID;
	}
	
	/* (non-Javadoc)
	 * @see descent.internal.ui.preferences.PropertyAndPreferencePage#getPropertyPageID()
	 */
	protected String getPropertyPageID() {
		return PROP_ID;
	}

}
