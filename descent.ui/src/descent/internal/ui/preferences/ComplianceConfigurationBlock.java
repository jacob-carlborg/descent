package descent.internal.ui.preferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.ControlEnableState;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

import descent.core.JavaCore;
import descent.internal.ui.dialogs.StatusInfo;
import descent.internal.ui.wizards.IStatusChangeListener;
import descent.internal.ui.wizards.buildpaths.BuildPathSupport;
import descent.launching.JavaRuntime;

/**
  */
public class ComplianceConfigurationBlock extends OptionsConfigurationBlock {
	
	private final static boolean ALLOW_USER_TO_ENABLE_SEMANTIC_ERRORS = true;
	
	private static final String[] trueFalse= new String[] { JavaCore.ENABLED, JavaCore.DISABLED };

	// Preference store keys, see JavaCore.getOptions
	private static final Key PREF_SOURCE= getJDTCoreKey(JavaCore.COMPILER_SOURCE);
	private static final Key PREF_VERSION_LEVEL= getJDTCoreKey(JavaCore.COMPILER_VERSION_LEVEL);
	private static final Key PREF_VERSION_IDENTIFIERS= getJDTCoreKey(JavaCore.COMPILER_VERSION_IDENTIFIERS);
	private static final Key PREF_DEBUG_LEVEL= getJDTCoreKey(JavaCore.COMPILER_DEBUG_LEVEL);
	private static final Key PREF_DEBUG_IDENTIFIERS= getJDTCoreKey(JavaCore.COMPILER_DEBUG_IDENTIFIERS);
	private static final Key PREF_SHOW_SEMANTIC_ERRORS= getJDTCoreKey(JavaCore.COMPILER_SHOW_SEMANTIC_ERRORS);
	private static final Key PREF_ENABLE_WARNINGS= getJDTCoreKey(JavaCore.COMPILER_ENABLE_WARNINGS);
	private static final Key PREF_ALLOW_DEPRECATED= getJDTCoreKey(JavaCore.COMPILER_ALLOW_DEPRECATED);
	private static final Key PREF_ANALYZE_TEMPLATES= getJDTCoreKey(JavaCore.COMPILER_ANALYZE_TEMPLATES);

	private static final Key INTR_DEFAULT_COMPLIANCE= getJDTUIKey("internal.default.compliance"); //$NON-NLS-1$

	// values
	private static final String VERSION_0_x= JavaCore.VERSION_0_x;
	private static final String VERSION_1_x= JavaCore.VERSION_1_x;
	private static final String VERSION_2_x= JavaCore.VERSION_2_x;
	
	private static final String DEFAULT_CONF= "default"; //$NON-NLS-1$
	private static final String USER_CONF= "user";	 //$NON-NLS-1$

	private ArrayList fComplianceControls;

	private String[] fRememberedUserCompliance;
	
	private static final int IDX_COMPLIANCE= 0;

	private IStatus fComplianceStatus;

	private Composite fControlsComposite;
	private ControlEnableState fBlockEnableState;

	public ComplianceConfigurationBlock(IStatusChangeListener context, IProject project, IWorkbenchPreferenceContainer container) {
		super(context, project, getKeys(), container);

		fBlockEnableState= null;
		fComplianceControls= new ArrayList();
			
		fComplianceStatus= new StatusInfo();
		
		fRememberedUserCompliance= new String[] { // caution: order depends on IDX_* constants
			getValue(PREF_SOURCE),
		};
	}
	
	private static Key[] getKeys() {
		return new Key[] {
				PREF_SOURCE,
				PREF_VERSION_LEVEL,
				PREF_VERSION_IDENTIFIERS,
				PREF_DEBUG_LEVEL,
				PREF_DEBUG_IDENTIFIERS,
				PREF_SHOW_SEMANTIC_ERRORS,
				PREF_ENABLE_WARNINGS,
				PREF_ALLOW_DEPRECATED,
				PREF_ANALYZE_TEMPLATES,
			};
	}
		
	/* (non-Javadoc)
	 * @see descent.internal.ui.preferences.OptionsConfigurationBlock#settingsUpdated()
	 */
	protected void settingsUpdated() {
		setValue(INTR_DEFAULT_COMPLIANCE, getCurrentCompliance());
		super.settingsUpdated();
	}
	
	
	/*
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(Composite)
	 */
	protected Control createContents(Composite parent) {
		setShell(parent.getShell());
		
		Composite complianceComposite= createComplianceTabContent(parent);
		
		validateSettings(null, null, null);
	
		return complianceComposite;
	}
	
	public void enablePreferenceContent(boolean enable) {
		if (fControlsComposite != null && !fControlsComposite.isDisposed()) {
			if (enable) {
				if (fBlockEnableState != null) {
					fBlockEnableState.restore();
					fBlockEnableState= null;
				}
			} else {
				if (fBlockEnableState == null) {
					fBlockEnableState= ControlEnableState.disable(fControlsComposite);
				}
			}
		}
	}
	
	private Composite createComplianceTabContent(Composite folder) {


		String[] values3456= new String[] { VERSION_0_x, VERSION_1_x, VERSION_2_x };
		String[] values3456Labels= new String[] {
			PreferencesMessages.ComplianceConfigurationBlock_version0x,  
			PreferencesMessages.ComplianceConfigurationBlock_version1x, 
			PreferencesMessages.ComplianceConfigurationBlock_version2x
		};

		final ScrolledPageContent sc1 = new ScrolledPageContent(folder);
		Composite composite= sc1.getBody();
		GridLayout layout= new GridLayout();
		layout.marginHeight= 0;
		layout.marginWidth= 0;
		composite.setLayout(layout);
		
		fControlsComposite= new Composite(composite, SWT.NONE);
		fControlsComposite.setFont(composite.getFont());
		fControlsComposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		
		layout= new GridLayout();
		layout.marginHeight= 0;
		layout.marginWidth= 0;
		layout.numColumns= 1;
		fControlsComposite.setLayout(layout);

		int nColumns= 3;

		layout= new GridLayout();
		layout.numColumns= nColumns;

		Group group= new Group(fControlsComposite, SWT.NONE);
		group.setFont(fControlsComposite.getFont());
		group.setText(PreferencesMessages.ComplianceConfigurationBlock_compliance_group_label); 
		group.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		group.setLayout(layout);
	
		String label= PreferencesMessages.ComplianceConfigurationBlock_compiler_compliance_label; 
		addComboBox(group, label, PREF_SOURCE, values3456, values3456Labels, 0);
		
		layout= new GridLayout();
		layout.numColumns= nColumns;

		group= new Group(fControlsComposite, SWT.NONE);
		group.setFont(fControlsComposite.getFont());
		group.setText("Compiler configuration"); 
		group.setLayoutData(new GridData(GridData.BEGINNING, GridData.END, true, false));
		group.setLayout(layout);
		
		addTextField(group, "Version level:", PREF_VERSION_LEVEL, 0, 20);
		addTextField(group, "Version identifiers:", PREF_VERSION_IDENTIFIERS, 0, 290);
		addTextField(group, "Debug level:", PREF_DEBUG_LEVEL, 0, 20);
		addTextField(group, "Debug identifiers:", PREF_DEBUG_IDENTIFIERS, 0, 290);
		
		layout= new GridLayout();
		layout.numColumns= nColumns;
		
		

		if (ALLOW_USER_TO_ENABLE_SEMANTIC_ERRORS) {
			group= new Group(fControlsComposite, SWT.NONE);
			group.setFont(fControlsComposite.getFont());
			group.setText("Semantic analysis"); 
			group.setLayoutData(new GridData(GridData.BEGINNING, GridData.END, true, false));
			group.setLayout(layout);
			
			addComboBox(group, "Show semantic errors", PREF_SHOW_SEMANTIC_ERRORS, new String[] { "0", "1", "2" }, new String[] { "No", "Some (EXPERIMENTAL)", "All (EXPERIMENTAL)" }, 0);
			addCheckBox(group, "Enable warnings", PREF_ENABLE_WARNINGS, trueFalse, 0);
			addCheckBox(group, "Allow deprecated features", PREF_ALLOW_DEPRECATED, trueFalse, 0);
			addCheckBox(group, "Analyze templates", PREF_ANALYZE_TEMPLATES, trueFalse, 0);
		
		//label= PreferencesMessages.ComplianceConfigurationBlock_default_settings_label; 
		//addCheckBox(group, label, INTR_DEFAULT_COMPLIANCE, new String[] { DEFAULT_CONF, USER_CONF }, 0);
		}
		
		return sc1;
	}
	
	protected final void openBuildPathPropertyPage() {
		if (getPreferenceContainer() != null) {
			Map data= new HashMap();
			data.put(BuildPathsPropertyPage.DATA_REVEAL_ENTRY, JavaRuntime.getDefaultJREContainerEntry());
			getPreferenceContainer().openPage(BuildPathsPropertyPage.PROP_ID, data);
		}
	}
	
	protected final void openJREInstallPreferencePage() {
		String jreID= BuildPathSupport.JRE_PREF_PAGE_ID;
		if (fProject == null && getPreferenceContainer() != null) {
			getPreferenceContainer().openPage(jreID, null);
		} else {
			PreferencesUtil.createPreferenceDialogOn(getShell(), jreID, new String[] { jreID }, null).open();
		}
	}

	/* (non-javadoc)
	 * Update fields and validate.
	 * @param changedKey Key that changed, or null, if all changed.
	 */	
	protected void validateSettings(Key changedKey, String oldValue, String newValue) {
		if (!areSettingsEnabled()) {
			return;
		}
		if (changedKey != null) {
			if (INTR_DEFAULT_COMPLIANCE.equals(changedKey)) {
				updateComplianceEnableState();
				updateComplianceDefaultSettings(true, null);
				fComplianceStatus= validateCompliance();
			} else if (PREF_SOURCE.equals(changedKey)) {
			    // set compliance settings to default
			    Object oldDefault= setValue(INTR_DEFAULT_COMPLIANCE, DEFAULT_CONF);
			    updateComplianceEnableState();
				updateComplianceDefaultSettings(USER_CONF.equals(oldDefault), oldValue);
				fComplianceStatus= validateCompliance();
			} else if (PREF_VERSION_LEVEL.equals(changedKey)) {
				IStatus status = validatePositiveNumber(newValue, 
					"The version level must be specified.", 
					"The version level must be positive or zero.", 
					"The version level must be a number");
				if (!status.isOK()) {
					fContext.statusChanged(status);
					return;
				}
			} else if (PREF_DEBUG_LEVEL.equals(changedKey)) {
				IStatus status = validatePositiveNumber(newValue, 
					"The debug level must be specified.", 
					"The debug level must be positive or zero.", 
					"The debug level must be a number");
				if (!status.isOK()) {
					fContext.statusChanged(status);
					return;
				}
			} else {
				return;
			}
		} else {
			updateComplianceEnableState();
			fComplianceStatus= validateCompliance();
		}		
		fContext.statusChanged(fComplianceStatus);
	}
	
	protected static IStatus validatePositiveNumber(final String number, String emptyError, String negativeError, String nanError) {

		final StatusInfo status= new StatusInfo();
		if (number.length() == 0) {
			status.setError(emptyError); 
		} else {
			try {
				final int value= Integer.parseInt(number);
				if (value < 0) {
					status.setError(negativeError); 
				}
			} catch (NumberFormatException exception) {
				status.setError(nanError); 
			}
		}
		return status;
	}
	
	private IStatus validateCompliance() {
		StatusInfo status= new StatusInfo();
		return status;
	}
			
	
	/* (non-Javadoc)
	 * @see descent.internal.ui.preferences.OptionsConfigurationBlock#useProjectSpecificSettings(boolean)
	 */
	public void useProjectSpecificSettings(boolean enable) {
		super.useProjectSpecificSettings(enable);
	}
		
	/*
	 * Update the compliance controls' enable state
	 */		
	private void updateComplianceEnableState() {
		boolean enabled= checkValue(INTR_DEFAULT_COMPLIANCE, USER_CONF);
		for (int i= fComplianceControls.size() - 1; i >= 0; i--) {
			Control curr= (Control) fComplianceControls.get(i);
			curr.setEnabled(enabled);
		}
	}

	/*
	 * Set the default compliance values derived from the chosen level
	 */	
	private void updateComplianceDefaultSettings(boolean rememberOld, String oldComplianceLevel) {
		boolean isDefault= checkValue(INTR_DEFAULT_COMPLIANCE, DEFAULT_CONF);
		String complianceLevel= getValue(PREF_SOURCE);
		
		if (isDefault) {
			if (rememberOld) {
				if (oldComplianceLevel == null) {
					oldComplianceLevel= complianceLevel;
				}
			}
		} else {
			if (rememberOld && complianceLevel.equals(fRememberedUserCompliance[IDX_COMPLIANCE])) {
			} else {
				return;
			}
		}
		updateControls();
	}
	
	/*
	 * Evaluate if the current compliance setting correspond to a default setting
	 */
	private String getCurrentCompliance() {
		return USER_CONF;
		/*
		Object complianceLevel= getValue(PREF_COMPLIANCE);
		if ((VERSION_2_x.equals(complianceLevel)
				&& IGNORE.equals(getValue(PREF_PB_ASSERT_AS_IDENTIFIER))
				&& IGNORE.equals(getValue(PREF_PB_ENUM_AS_IDENTIFIER))
				&& VERSION_2_x.equals(getValue(PREF_SOURCE_COMPATIBILITY))
				&& VERSION_0_x.equals(getValue(PREF_CODEGEN_TARGET_PLATFORM)))
			|| (VERSION_1_4.equals(complianceLevel)
				&& WARNING.equals(getValue(PREF_PB_ASSERT_AS_IDENTIFIER))
				&& WARNING.equals(getValue(PREF_PB_ENUM_AS_IDENTIFIER))
				&& VERSION_2_x.equals(getValue(PREF_SOURCE_COMPATIBILITY))
				&& VERSION_1_x.equals(getValue(PREF_CODEGEN_TARGET_PLATFORM)))
			|| (VERSION_1_5.equals(complianceLevel)
				&& ERROR.equals(getValue(PREF_PB_ASSERT_AS_IDENTIFIER))
				&& ERROR.equals(getValue(PREF_PB_ENUM_AS_IDENTIFIER))
				&& VERSION_1_5.equals(getValue(PREF_SOURCE_COMPATIBILITY))
				&& VERSION_1_5.equals(getValue(PREF_CODEGEN_TARGET_PLATFORM)))
			|| (VERSION_1_6.equals(complianceLevel)
				&& ERROR.equals(getValue(PREF_PB_ASSERT_AS_IDENTIFIER))
				&& ERROR.equals(getValue(PREF_PB_ENUM_AS_IDENTIFIER))
				&& VERSION_1_6.equals(getValue(PREF_SOURCE_COMPATIBILITY))
				&& VERSION_1_6.equals(getValue(PREF_CODEGEN_TARGET_PLATFORM)))) {
			return DEFAULT_CONF;
		}
		return USER_CONF;
		*/
	}
	
	
	protected String[] getFullBuildDialogStrings(boolean workspaceSettings) {
		String title= PreferencesMessages.ComplianceConfigurationBlock_needsbuild_title; 
		String message;
		if (workspaceSettings) {
			message= PreferencesMessages.ComplianceConfigurationBlock_needsfullbuild_message; 
		} else {
			message= PreferencesMessages.ComplianceConfigurationBlock_needsprojectbuild_message; 
		}
		return new String[] { title, message };
	}
		
}
