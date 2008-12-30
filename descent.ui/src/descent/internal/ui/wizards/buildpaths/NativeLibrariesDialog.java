package descent.internal.ui.wizards.buildpaths;

import org.eclipse.core.runtime.IStatus;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.jface.dialogs.StatusDialog;

import descent.internal.ui.preferences.NativeLibrariesConfigurationBlock;
import descent.internal.ui.wizards.IStatusChangeListener;
import descent.internal.ui.wizards.NewWizardMessages;

/**
 * 
 */
public class NativeLibrariesDialog extends StatusDialog {

	private final NativeLibrariesConfigurationBlock fConfigurationBlock;
	
	public NativeLibrariesDialog(Shell parent, CPListElement selElement) {
		super(parent);
		setTitle(NewWizardMessages.NativeLibrariesDialog_title);
		
		setShellStyle(getShellStyle() | SWT.RESIZE);
		
		IStatusChangeListener listener= new IStatusChangeListener() {
			public void statusChanged(IStatus status) {
				updateStatus(status);
			}
		};	
		
		fConfigurationBlock= new NativeLibrariesConfigurationBlock(listener, parent, selElement);
		setHelpAvailable(false);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createDialogArea(Composite parent) {
		Composite composite= (Composite) super.createDialogArea(parent);
		Control inner= fConfigurationBlock.createContents(composite);
		inner.setLayoutData(new GridData(GridData.FILL_BOTH));
		applyDialogFont(composite);		
		return composite;
	}

	public String getNativeLibraryPath() {
		return fConfigurationBlock.getNativeLibraryPath();
	}

}
