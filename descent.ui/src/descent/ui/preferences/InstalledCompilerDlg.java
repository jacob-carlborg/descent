package descent.ui.preferences;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;

import com.ibm.icu.text.MessageFormat;

import descent.internal.ui.JavaPlugin;
import descent.internal.ui.util.StatusInfo;
import descent.internal.ui.wizards.dialogfields.ComboDialogField;
import descent.internal.ui.wizards.dialogfields.DialogField;
import descent.internal.ui.wizards.dialogfields.IDialogFieldListener;
import descent.internal.ui.wizards.dialogfields.IStringButtonAdapter;
import descent.internal.ui.wizards.dialogfields.StringButtonDialogField;
import descent.internal.ui.wizards.dialogfields.StringDialogField;

public class InstalledCompilerDlg extends StatusDialog{
	
	private InstalledCompilerDlgRequestor fRequestor;
	private DCInstallation fEditedVM;
	private ComboDialogField fVMTypeCombo;
	
	private StringButtonDialogField fJRERoot;
	private StringDialogField fVMName;
	
	private StringDialogField fVMArgs;
	private DCompilerType[] fVMTypes;
	private DCompilerType fSelectedVMType;
	

	private IStatus[] fStati;
	private int fPrevIndex = -1;
	

	public InstalledCompilerDlg(InstalledCompilerDlgRequestor requestor, Shell shell, DCompilerType[] vmInstallTypes, DCInstallation editedVM) {
		super(shell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		fRequestor= requestor;
		fStati= new IStatus[5];
		for (int i= 0; i < fStati.length; i++) {
			fStati[i]= new StatusInfo();
		}
		
		fVMTypes= vmInstallTypes;
		fSelectedVMType= editedVM != null ? editedVM.getDCompilerType() : vmInstallTypes[0];
		
		fEditedVM= editedVM;
	}
	
	/**
	 * @see Windows#configureShell
	 */
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		//PlatformUI.getWorkbench().getHelpSystem().setHelp(newShell, IJavaDebugHelpContextIds.EDIT_JRE_DIALOG);
	}		
	

	protected void createDialogFields() {
		fVMTypeCombo= new ComboDialogField(SWT.READ_ONLY);
		fVMTypeCombo.setLabelText(DCMessages.installedCompilerDlg_dcType); 
		fVMTypeCombo.setItems(getDCTypeNames());
		
		fVMName= new StringDialogField();
		fVMName.setLabelText(DCMessages.installedCompilerDlg_dcName); 
		
		fJRERoot= new StringButtonDialogField(new IStringButtonAdapter() {
			public void changeControlPressed(DialogField field) {
				browseForInstallDir();
			}
		});
		fJRERoot.setLabelText(DCMessages.installedCompilerDlg_dcHome); 
		fJRERoot.setButtonLabel(DCMessages.installedCompilerDlg_dcHomeBrowse); 
			
		fVMArgs= new StringDialogField();
		fVMArgs.setLabelText(DCMessages.installedCompilerDlg_defArgs); 
	}

	protected void createFieldListeners() {
		fVMTypeCombo.setDialogFieldListener(new IDialogFieldListener() {
			public void dialogFieldChanged(DialogField field) {
				updateVMType();
			}
		});
		
		fVMName.setDialogFieldListener(new IDialogFieldListener() {
			public void dialogFieldChanged(DialogField field) {
				setVMNameStatus(validateVMName());
				updateStatusLine();
			}
		});
		
		fJRERoot.setDialogFieldListener(new IDialogFieldListener() {
			public void dialogFieldChanged(DialogField field) {
				setJRELocationStatus(validateJRELocation());
				updateStatusLine();
			}
		});
	
	}
	
	private String[] getDCTypeNames() {
		return new String[]{ "DMD Windows", "DMD Linux", "GDC" };
	}

	protected Control createDialogArea(Composite ancestor) {
		createDialogFields();
		Composite parent = (Composite)super.createDialogArea(ancestor);
		((GridLayout)parent.getLayout()).numColumns= 3;
		
		fVMTypeCombo.doFillIntoGrid(parent, 3);
		((GridData)fVMTypeCombo.getComboControl(null).getLayoutData()).widthHint= convertWidthInCharsToPixels(50);

		fVMName.doFillIntoGrid(parent, 3);
	
		fJRERoot.doFillIntoGrid(parent, 3);
		
		fVMArgs.doFillIntoGrid(parent, 3);
		((GridData)fVMArgs.getTextControl(null).getLayoutData()).widthHint= convertWidthInCharsToPixels(50);
		
//		Label l = new Label(parent, SWT.NONE);
//		l.setText(JREMessages.AddVMDialog_JRE_system_libraries__1); 
//		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
//		gd.horizontalSpan = 3;
//		l.setLayoutData(gd);	
//		
//		fLibraryBlock = new VMLibraryBlock(this);
//		Control block = fLibraryBlock.createControl(parent);
//		gd = new GridData(GridData.FILL_BOTH);
//		gd.horizontalSpan = 3;
//		block.setLayoutData(gd);
//		
//		Text t= fJRERoot.getTextControl(parent);
//		gd= (GridData)t.getLayoutData();
//		gd.grabExcessHorizontalSpace=true;
//		gd.widthHint= convertWidthInCharsToPixels(50);
		
		initializeFields();
		createFieldListeners();
		applyDialogFont(parent);
		return parent;
	}

	private void initializeFields() {
		fVMTypeCombo.setItems(getDCTypeNames());
		if (fEditedVM == null) {
			fVMName.setText(""); //$NON-NLS-1$
			fJRERoot.setText(""); //$NON-NLS-1$
			fVMArgs.setText(""); //$NON-NLS-1$
		} else {
			fVMTypeCombo.setEnabled(false);
			fVMName.setText(fEditedVM.getName());
			fJRERoot.setText(fEditedVM.getInstallLocation().getAbsolutePath());
			String[] vmArgs = fEditedVM.getDefaultArguments();
			if (vmArgs != null) {
				StringBuffer buffer = new StringBuffer();
				int length= vmArgs.length;
				if (length > 0) {
					buffer.append(vmArgs[0]);
					for (int i = 1; i < length; i++) {
						buffer.append(' ').append(vmArgs[i]);
					}
				}
				fVMArgs.setText(buffer.toString());
			}				
		}
		setVMNameStatus(validateVMName());
		updateStatusLine();
	}
	private void updateVMType() {
		int selIndex= fVMTypeCombo.getSelectionIndex();
		if (selIndex == fPrevIndex) {
			return;
		}
		fPrevIndex = selIndex;
		if (selIndex >= 0 && selIndex < fVMTypes.length) {
			fSelectedVMType= fVMTypes[selIndex];
		}
		setJRELocationStatus(validateJRELocation());
		updateStatusLine();
	}
	
	private DCompilerType getVMType() {
		return fSelectedVMType;
	}
	
	public void create() {
		super.create();
		fVMName.setFocus();
		selectVMType();  
	}
	
	private String[] getVMTypeNames() {
		String[] names=  new String[fVMTypes.length];
		for (int i= 0; i < fVMTypes.length; i++) {
			names[i]= fVMTypes[i].getName();
		}
		return names;
	}
	

	private void selectVMType() {
		for (int i= 0; i < fVMTypes.length; i++) {
			if (fSelectedVMType == fVMTypes[i]) {
				fVMTypeCombo.selectItem(i);
				return;
			}
		}
	}
	protected void okPressed() {
		doOkPressed();
		super.okPressed();
	}
	
	private void doOkPressed() {
//		if (fEditedVM == null) {
//			IVMInstall vm= new VMStandin(fSelectedVMType, createUniqueId(fSelectedVMType));
//			setFieldValuesToVM(vm);
//			fRequestor.vmAdded(vm);
//		} else {
//			setFieldValuesToVM(fEditedVM);
//		}
	}
	

	private IStatus validateJRELocation() {
		String locationName= fJRERoot.getText();
		IStatus s = null;
		File file = null;
		if (locationName.length() == 0) {
			s = new StatusInfo(IStatus.INFO, DCMessages.installedCompilerDlg_enterLocation); 
		} else {
			file= new File(locationName);
			if (!file.exists()) {
				s = new StatusInfo(IStatus.ERROR, DCMessages.installedCompilerDlg_locationNotExists); 
			} else {
				final IStatus[] temp = new IStatus[1];
				final File tempFile = file; 
				Runnable r = new Runnable() {
					/**
					 * @see java.lang.Runnable#run()
					 */
					public void run() {
						temp[0] = getVMType().validateInstallLocation(tempFile);
					}
				};
				BusyIndicator.showWhile(getShell().getDisplay(), r);
				s = temp[0];
			}
		}
		if (s.isOK()) {
			String name = fVMName.getText();
			if (name == null || name.trim().length() == 0) {
				// auto-generate VM name
				try {
					String genName = null;
					IPath path = new Path(file.getCanonicalPath());
					int segs = path.segmentCount();
					if (segs == 1) {
						genName = path.segment(0);
					} else if (segs >= 2) {
						String last = path.lastSegment();
						if ("jre".equalsIgnoreCase(last)) { //$NON-NLS-1$
							genName = path.segment(segs - 2);
						} else {
							genName = last;
						}
					}
					if (genName != null) {
						fVMName.setText(genName);
					}
				} catch (IOException e) {}
			}
		} else {
		}
		return s;
	}
	

	protected void updateStatusLine() {
		IStatus max= null;
		for (int i= 0; i < fStati.length; i++) {
			IStatus curr= fStati[i];
			if (curr.matches(IStatus.ERROR)) {
				updateStatus(curr);
				return;
			}
			if (max == null || curr.getSeverity() > max.getSeverity()) {
				max= curr;
			}
		}
		updateStatus(max);
	}
			
	private void browseForInstallDir() {
		DirectoryDialog dialog= new DirectoryDialog(getShell());
		dialog.setFilterPath(fJRERoot.getText());
		dialog.setMessage(DCMessages.installedCompilerDlg_pickDCRootDialog_message); 
		String newPath= dialog.open();
		if (newPath != null) {
			fJRERoot.setText(newPath);
		}
	}

	private IStatus validateVMName() {
		StatusInfo status= new StatusInfo();
		String name= fVMName.getText();
		if (name == null || name.trim().length() == 0) {
			status.setInfo(DCMessages.installedCompilerDlg_enterName); 
		} else {
			if (fRequestor.isDuplicateName(name) && (fEditedVM == null || !name.equals(fEditedVM.getName()))) {
				status.setError(DCMessages.installedCompilerDlg_duplicateName); 
			} else {
				IStatus s = ResourcesPlugin.getWorkspace().validateName(name, IResource.FILE);
				if (!s.isOK()) {
					status.setError(
							MessageFormat.format(DCMessages.installedCompilerDlg_JRE_name_must_be_a_valid_file_name___0__1, new String[]{s.getMessage()})
							); 
				}
			}
		}
		return status;
	}
	protected File getAbsoluteFileOrEmpty(String path) {
		if (path == null || path.length() == 0) {
			return new File(""); //$NON-NLS-1$
		}
		return new File(path).getAbsoluteFile();
	}
	
	private void setVMNameStatus(IStatus status) {
		fStati[0]= status;
	}
	
	private void setJRELocationStatus(IStatus status) {
		fStati[1]= status;
	}
	
	protected IStatus getSystemLibraryStatus() {
		return fStati[3];
	}
	
	protected void setSystemLibraryStatus(IStatus status) {
		fStati[3]= status;
	}
	
	/**
	 * Updates the status of the ok button to reflect the given status.
	 * Subclasses may override this method to update additional buttons.
	 * @param status the status.
	 */
	protected void updateButtonsEnableState(IStatus status) {
		Button ok = getButton(IDialogConstants.OK_ID);
		if (ok != null && !ok.isDisposed())
			ok.setEnabled(status.getSeverity() == IStatus.OK);
	}	
	
	/**
	 * @see org.eclipse.jface.dialogs.Dialog#setButtonLayoutData(org.eclipse.swt.widgets.Button)
	 */
	protected void setButtonLayoutData(Button button) {
		super.setButtonLayoutData(button);
	}
	
	/**
	 * Returns the name of the section that this dialog stores its settings in
	 * 
	 * @return String
	 */
	protected String getDialogSettingsSectionName() {
		return "ADD_VM_DIALOG_SECTION"; //$NON-NLS-1$
	}
	
	 /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#getDialogBoundsSettings()
     */
    protected IDialogSettings getDialogBoundsSettings() {
    	 IDialogSettings settings = JavaPlugin.getDefault().getDialogSettings();
         IDialogSettings section = settings.getSection(getDialogSettingsSectionName());
         if (section == null) {
             section = settings.addNewSection(getDialogSettingsSectionName());
         } 
         return section;
    }

}
