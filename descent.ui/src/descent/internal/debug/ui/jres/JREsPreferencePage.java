package descent.internal.debug.ui.jres;


import java.io.File;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

import descent.core.IJavaModel;
import descent.core.JavaCore;
import descent.debug.ui.IJavaDebugUIConstants;
import descent.internal.debug.ui.IJavaDebugHelpContextIds;
import descent.internal.ui.JavaPlugin;
import descent.launching.IVMInstall;
import descent.launching.JavaRuntime;
import descent.launching.LibraryLocation;

/**
 * The Installed JREs preference page.
 * 
 * @since 3.0
 */
public class JREsPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
							
	// JRE Block
	private InstalledJREsBlock fJREBlock;									
		
	public JREsPreferencePage() {
		super();
		
		// only used when page is shown programatically
		setTitle(JREMessages.JREsPreferencePage_1);	 
		
		setDescription(JREMessages.JREsPreferencePage_2); 
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}
	
	/**
	 * Find & verify the default VM.
	 */
	private void initDefaultVM() {
		IVMInstall realDefault= JavaRuntime.getDefaultVMInstall();
		if (realDefault != null) {
			IVMInstall[] vms= fJREBlock.getJREs();
			for (int i = 0; i < vms.length; i++) {
				IVMInstall fakeVM= vms[i];
				if (fakeVM.equals(realDefault)) {
					verifyDefaultVM(fakeVM);
					break;
				}
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createContents(Composite ancestor) {
		initializeDialogUnits(ancestor);
		
		noDefaultAndApplyButton();
		
		GridLayout layout= new GridLayout();
		layout.numColumns= 1;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		ancestor.setLayout(layout);
					
		fJREBlock = new InstalledJREsBlock();
		fJREBlock.createControl(ancestor);
		Control control = fJREBlock.getControl();
		GridData data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 1;
		control.setLayoutData(data);
		
		fJREBlock.restoreColumnSettings(JavaPlugin.getDefault().getDialogSettings(), IJavaDebugHelpContextIds.JRE_PREFERENCE_PAGE);
						
		PlatformUI.getWorkbench().getHelpSystem().setHelp(ancestor, IJavaDebugHelpContextIds.JRE_PREFERENCE_PAGE);		
		initDefaultVM();
		fJREBlock.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				IVMInstall install = getCurrentDefaultVM();
				if (install == null) {
					setValid(false);
					setErrorMessage(JREMessages.JREsPreferencePage_13); 
				} else {
					setValid(true);
					setErrorMessage(null);
				}
			}
		});
		applyDialogFont(ancestor);
		return ancestor;
	}
			
	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.IPreferencePage#performOk()
	 */
	public boolean performOk() {
		final boolean[] canceled = new boolean[] {false};
		BusyIndicator.showWhile(null, new Runnable() {
			public void run() {
				IVMInstall defaultVM = getCurrentDefaultVM();
				IVMInstall[] vms = fJREBlock.getJREs();
				JREsUpdater updater = new JREsUpdater();
				if (!updater.updateJRESettings(vms, defaultVM)) {
					canceled[0] = true;
				}
			}
		});
		
		if(canceled[0]) {
			return false;
		}
		
		// save column widths
		IDialogSettings settings = JavaPlugin.getDefault().getDialogSettings();
		fJREBlock.saveColumnSettings(settings, IJavaDebugHelpContextIds.JRE_PREFERENCE_PAGE);
		
		return super.performOk();
	}	
	
	protected IJavaModel getJavaModel() {
		return JavaCore.create(ResourcesPlugin.getWorkspace().getRoot());
	}

	/**
	 * Verify that the specified VM can be a valid default VM.  This amounts to verifying
	 * that all of the VM's library locations exist on the file system.  If this fails,
	 * remove the VM from the table and try to set another default.
	 */
	private void verifyDefaultVM(IVMInstall vm) {
		if (vm != null) {
			
			// Verify that all of the specified VM's library locations actually exist
			LibraryLocation[] locations= JavaRuntime.getLibraryLocations(vm);
			boolean exist = true;
			for (int i = 0; i < locations.length; i++) {
				exist = exist && new File(locations[i].getSystemLibraryPath().toOSString()).exists();
			}
			
			// If all library locations exist, check the corresponding entry in the list,
			// otherwise remove the VM
			if (exist) {
				fJREBlock.setCheckedJRE(vm);
			} else {
				fJREBlock.removeJREs(new IVMInstall[]{vm});
				IVMInstall def = JavaRuntime.getDefaultVMInstall();
				if (def == null) {
					fJREBlock.setCheckedJRE(null);
				} else {
					fJREBlock.setCheckedJRE(def);
				}
				ErrorDialog.openError(getControl().getShell(), JREMessages.JREsPreferencePage_1, JREMessages.JREsPreferencePage_10, new Status(IStatus.ERROR, IJavaDebugUIConstants.PLUGIN_ID, IJavaDebugUIConstants.INTERNAL_ERROR, JREMessages.JREsPreferencePage_11, null)); //  
				return;
			}
		} else {
			fJREBlock.setCheckedJRE(null);
		}
	}
	
	private IVMInstall getCurrentDefaultVM() {
		return fJREBlock.getCheckedJRE();
	}	
	
}
