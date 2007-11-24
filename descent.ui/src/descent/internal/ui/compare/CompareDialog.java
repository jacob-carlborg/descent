package descent.internal.ui.compare;

import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import org.eclipse.jface.dialogs.*;
import org.eclipse.jface.viewers.Viewer;

import org.eclipse.ui.PlatformUI;

import org.eclipse.compare.*;
import org.eclipse.compare.structuremergeviewer.ICompareInput;

import descent.internal.ui.IJavaHelpContextIds;


class CompareDialog extends ResizableDialog {
	
	class ViewerSwitchingPane extends CompareViewerSwitchingPane {
		
		ViewerSwitchingPane(Composite parent, int style) {
			super(parent, style, false);
		}
	
		protected Viewer getViewer(Viewer oldViewer, Object input) {
			if (input instanceof ICompareInput)
				return CompareUI.findContentViewer(oldViewer, (ICompareInput)input, this, fCompareConfiguration);
			return null;
		}
				
		public void setImage(Image image) {
			// don't show icon
		}
	}
	
	private CompareViewerSwitchingPane fContentPane;
	private CompareConfiguration fCompareConfiguration;
	private ICompareInput fInput;
	
	
	CompareDialog(Shell parent, ResourceBundle bundle) {
		super(parent, bundle);		
		
		fCompareConfiguration= new CompareConfiguration();
		fCompareConfiguration.setLeftEditable(false);
		fCompareConfiguration.setRightEditable(false);
	}
	
	void compare(ICompareInput input) {
		
		fInput= input;
		
		fCompareConfiguration.setLeftLabel(fInput.getLeft().getName());
		fCompareConfiguration.setLeftImage(fInput.getLeft().getImage());
		
		fCompareConfiguration.setRightLabel(fInput.getRight().getName());
		fCompareConfiguration.setRightImage(fInput.getRight().getImage());
		
		if (fContentPane != null)
			fContentPane.setInput(fInput);
			
		open();
	}
	
	 /* (non Javadoc)
 	 * Creates SWT control tree.
 	 */
	protected synchronized Control createDialogArea(Composite parent2) {
		
		Composite parent= (Composite) super.createDialogArea(parent2);

		getShell().setText(JavaCompareUtilities.getString(fBundle, "title")); //$NON-NLS-1$
		
		fContentPane= new ViewerSwitchingPane(parent, SWT.BORDER | SWT.FLAT);
		fContentPane.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL
					| GridData.VERTICAL_ALIGN_FILL | GridData.GRAB_VERTICAL));
		
		if (fInput != null)
			fContentPane.setInput(fInput);
			
		applyDialogFont(parent);		
		return parent;
	}
	
	/* (non-Javadoc)
	 * Method declared on Dialog.
	 */
	protected void createButtonsForButtonBar(Composite parent) {
		String buttonLabel= JavaCompareUtilities.getString(fBundle, "buttonLabel", IDialogConstants.OK_LABEL); //$NON-NLS-1$
		createButton(parent, IDialogConstants.CANCEL_ID, buttonLabel, false);
	}

	/*
	 * @see org.eclipse.jface.window.Window#configureShell(Shell)
	 */
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(newShell, IJavaHelpContextIds.COMPARE_DIALOG);
	}
}
