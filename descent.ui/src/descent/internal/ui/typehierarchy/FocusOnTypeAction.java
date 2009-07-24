package descent.internal.ui.typehierarchy;

import org.eclipse.swt.widgets.Shell;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;

import org.eclipse.ui.PlatformUI;

import descent.core.IType;
import descent.core.search.IJavaSearchConstants;
import descent.core.search.SearchEngine;

import descent.internal.ui.IJavaHelpContextIds;
import descent.internal.ui.dialogs.TypeSelectionDialog2;

/**
 * Refocuses the type hierarchy on a type selection from a all types dialog.
 */
public class FocusOnTypeAction extends Action {
			
	private TypeHierarchyViewPart fViewPart;
	
	public FocusOnTypeAction(TypeHierarchyViewPart part) {
		super(TypeHierarchyMessages.FocusOnTypeAction_label); 
		setDescription(TypeHierarchyMessages.FocusOnTypeAction_description); 
		setToolTipText(TypeHierarchyMessages.FocusOnTypeAction_tooltip); 
		
		fViewPart= part;
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this,	IJavaHelpContextIds.FOCUS_ON_TYPE_ACTION);
	}

	/*
	 * @see Action#run
	 */
	public void run() {
		Shell parent= fViewPart.getSite().getShell();
		TypeSelectionDialog2 dialog= new TypeSelectionDialog2(parent, false, 
			PlatformUI.getWorkbench().getProgressService(), 
			SearchEngine.createWorkspaceScope(), IJavaSearchConstants.TYPE);
	
		dialog.setTitle(TypeHierarchyMessages.FocusOnTypeAction_dialog_title); 
		dialog.setMessage(TypeHierarchyMessages.FocusOnTypeAction_dialog_message); 
		if (dialog.open() != IDialogConstants.OK_ID) {
			return;
		}
		
		Object[] types= dialog.getResult();
		if (types != null && types.length > 0) {
			IType type= (IType)types[0];
			fViewPart.setInputElement(type);
		}
	}	
}
