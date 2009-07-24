package descent.internal.ui.typehierarchy;

import org.eclipse.swt.custom.BusyIndicator;

import org.eclipse.jface.action.Action;

import org.eclipse.ui.PlatformUI;

import descent.internal.ui.IJavaHelpContextIds;
import descent.internal.ui.JavaPluginImages;

/**
 * Action to show / hide inherited members in the method view
 * Depending in the action state a different label provider is installed in the viewer
 */
public class ShowInheritedMembersAction extends Action {
	
	private MethodsViewer fMethodsViewer;
	
	/** 
	 * Creates the action.
	 */
	public ShowInheritedMembersAction(MethodsViewer viewer, boolean initValue) {
		super(TypeHierarchyMessages.ShowInheritedMembersAction_label); 
		setDescription(TypeHierarchyMessages.ShowInheritedMembersAction_description); 
		setToolTipText(TypeHierarchyMessages.ShowInheritedMembersAction_tooltip); 
		
		JavaPluginImages.setLocalImageDescriptors(this, "inher_co.gif"); //$NON-NLS-1$

		fMethodsViewer= viewer;
		
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IJavaHelpContextIds.SHOW_INHERITED_ACTION);
 
		setChecked(initValue);
	}
	
	/*
	 * @see Action#actionPerformed
	 */	
	public void run() {
		BusyIndicator.showWhile(fMethodsViewer.getControl().getDisplay(), new Runnable() {
			public void run() {
				fMethodsViewer.showInheritedMethods(isChecked());
			}
		});		
	}	
}
