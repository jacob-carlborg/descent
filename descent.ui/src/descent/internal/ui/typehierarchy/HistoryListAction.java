package descent.internal.ui.typehierarchy;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.IStatus;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;

import org.eclipse.ui.PlatformUI;

import descent.core.IJavaElement;

import descent.ui.JavaElementLabelProvider;

import descent.internal.ui.IJavaHelpContextIds;
import descent.internal.ui.JavaPlugin;
import descent.internal.ui.dialogs.StatusInfo;
import descent.internal.ui.wizards.dialogfields.DialogField;
import descent.internal.ui.wizards.dialogfields.IListAdapter;
import descent.internal.ui.wizards.dialogfields.LayoutUtil;
import descent.internal.ui.wizards.dialogfields.ListDialogField;

public class HistoryListAction extends Action {
	
	private class HistoryListDialog extends StatusDialog {
		
		private ListDialogField fHistoryList;
		private IStatus fHistoryStatus;
		private IJavaElement fResult;
		
		private HistoryListDialog(Shell shell, IJavaElement[] elements) {
			super(shell);
			setTitle(TypeHierarchyMessages.HistoryListDialog_title); 
			
			String[] buttonLabels= new String[] { 
				TypeHierarchyMessages.HistoryListDialog_remove_button, 
			};
					
			IListAdapter adapter= new IListAdapter() {
				public void customButtonPressed(ListDialogField field, int index) {
					doCustomButtonPressed();
				}
				public void selectionChanged(ListDialogField field) {
					doSelectionChanged();
				}
				
				public void doubleClicked(ListDialogField field) {
					doDoubleClicked();
				}				
			};
		
			JavaElementLabelProvider labelProvider= new JavaElementLabelProvider(JavaElementLabelProvider.SHOW_QUALIFIED | JavaElementLabelProvider.SHOW_ROOT);
			
			fHistoryList= new ListDialogField(adapter, buttonLabels, labelProvider);
			fHistoryList.setLabelText(TypeHierarchyMessages.HistoryListDialog_label); 
			fHistoryList.setElements(Arrays.asList(elements));
			
			ISelection sel;
			if (elements.length > 0) {
				sel= new StructuredSelection(elements[0]);
			} else {
				sel= new StructuredSelection();
			}
			
			fHistoryList.selectElements(sel);
		}

			
		/*
		 * @see Dialog#createDialogArea(Composite)
		 */
		protected Control createDialogArea(Composite parent) {
			initializeDialogUnits(parent);
			
			Composite composite= (Composite) super.createDialogArea(parent);
			
			Composite inner= new Composite(composite, SWT.NONE);
			inner.setFont(parent.getFont());
			
			inner.setLayoutData(new GridData(GridData.FILL_BOTH));

			LayoutUtil.doDefaultLayout(inner, new DialogField[] { fHistoryList }, true, 0, 0);
			LayoutUtil.setHeightHint(fHistoryList.getListControl(null), convertHeightInCharsToPixels(12));
			LayoutUtil.setHorizontalGrabbing(fHistoryList.getListControl(null));

			applyDialogFont(composite);		
			return composite;
		}

		/**
		 * Method doCustomButtonPressed.
		 */
		private void doCustomButtonPressed() {
			fHistoryList.removeElements(fHistoryList.getSelectedElements());
		}
		
		private void doDoubleClicked() {
			if (fHistoryStatus.isOK()) {
				okPressed();
			}
		}
		
		
		private void doSelectionChanged() {
			StatusInfo status= new StatusInfo();
			List selected= fHistoryList.getSelectedElements();
			if (selected.size() != 1) {
				status.setError(""); //$NON-NLS-1$
				fResult= null;
			} else {
				fResult= (IJavaElement) selected.get(0);
			}
			fHistoryList.enableButton(0, fHistoryList.getSize() > selected.size() && selected.size() != 0);			
			fHistoryStatus= status;
			updateStatus(status);	
		}
				
		public IJavaElement getResult() {
			return fResult;
		}
		
		public IJavaElement[] getRemaining() {
			List elems= fHistoryList.getElements();
			return (IJavaElement[]) elems.toArray(new IJavaElement[elems.size()]);
		}	
		
		/*
		 * @see org.eclipse.jface.window.Window#configureShell(Shell)
		 */
		protected void configureShell(Shell newShell) {
			super.configureShell(newShell);
			PlatformUI.getWorkbench().getHelpSystem().setHelp(newShell, IJavaHelpContextIds.HISTORY_LIST_DIALOG);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.window.Window#create()
		 */
		public void create() {
			setShellStyle(getShellStyle() | SWT.RESIZE);
			super.create();
		}

	}
	
	private TypeHierarchyViewPart fView;
	
	public HistoryListAction(TypeHierarchyViewPart view) {
		fView= view;
		setText(TypeHierarchyMessages.HistoryListAction_label); 
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IJavaHelpContextIds.HISTORY_LIST_ACTION);
	}
		
	/*
	 * @see IAction#run()
	 */
	public void run() {
		IJavaElement[] historyEntries= fView.getHistoryEntries();
		HistoryListDialog dialog= new HistoryListDialog(JavaPlugin.getActiveWorkbenchShell(), historyEntries);
		if (dialog.open() == Window.OK) {
			fView.setHistoryEntries(dialog.getRemaining());
			fView.setInputElement(dialog.getResult());
		}
	}

}

