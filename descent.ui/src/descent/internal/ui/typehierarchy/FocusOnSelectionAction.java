package descent.internal.ui.typehierarchy;

import descent.core.IType;

import descent.internal.corext.util.Messages;

import descent.ui.JavaElementLabels;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;

import org.eclipse.ui.PlatformUI;

import descent.internal.ui.IJavaHelpContextIds;
import descent.internal.ui.util.SelectionUtil;

/**
 * Refocuses the type hierarchy on the currently selection type.
 */
public class FocusOnSelectionAction extends Action {
		
	private TypeHierarchyViewPart fViewPart;
	
	public FocusOnSelectionAction(TypeHierarchyViewPart part) {
		super(TypeHierarchyMessages.FocusOnSelectionAction_label); 
		setDescription(TypeHierarchyMessages.FocusOnSelectionAction_description); 
		setToolTipText(TypeHierarchyMessages.FocusOnSelectionAction_tooltip); 
		fViewPart= part;
		
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IJavaHelpContextIds.FOCUS_ON_SELECTION_ACTION);
	}
	
	private ISelection getSelection() {
		ISelectionProvider provider= fViewPart.getSite().getSelectionProvider();
		if (provider != null) {
			return provider.getSelection();
		}
		return null;
	}
	

	/*
	 * @see Action#run
	 */
	public void run() {
		Object element= SelectionUtil.getSingleElement(getSelection());
		if (element instanceof IType) {
			fViewPart.setInputElement((IType)element);
		}
	}	
	
	public boolean canActionBeAdded() {
		Object element= SelectionUtil.getSingleElement(getSelection());
		if (element instanceof IType) {
			IType type= (IType)element;
			setText(Messages.format(
					TypeHierarchyMessages.FocusOnSelectionAction_label, 
					JavaElementLabels.getTextLabel(type, 0))); 
			return true;
		}
		return false;
	}
}
