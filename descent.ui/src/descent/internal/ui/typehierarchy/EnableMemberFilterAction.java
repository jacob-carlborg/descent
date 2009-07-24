package descent.internal.ui.typehierarchy;

import org.eclipse.swt.custom.BusyIndicator;

import org.eclipse.jface.action.Action;

import org.eclipse.ui.PlatformUI;

import descent.internal.ui.IJavaHelpContextIds;
import descent.internal.ui.JavaPluginImages;

/**
 * Action enable / disable member filtering
 */
public class EnableMemberFilterAction extends Action {

	private TypeHierarchyViewPart fView;	
	
	public EnableMemberFilterAction(TypeHierarchyViewPart v, boolean initValue) {
		super(TypeHierarchyMessages.EnableMemberFilterAction_label); 
		setDescription(TypeHierarchyMessages.EnableMemberFilterAction_description); 
		setToolTipText(TypeHierarchyMessages.EnableMemberFilterAction_tooltip); 
		
		JavaPluginImages.setLocalImageDescriptors(this, "impl_co.gif"); //$NON-NLS-1$

		fView= v;
		setChecked(initValue);
		
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IJavaHelpContextIds.ENABLE_METHODFILTER_ACTION);
		
	}

	/*
	 * @see Action#actionPerformed
	 */		
	public void run() {
		BusyIndicator.showWhile(fView.getSite().getShell().getDisplay(), new Runnable() {
			public void run() {
				fView.enableMemberFilter(isChecked());
			}
		});
	}
}
