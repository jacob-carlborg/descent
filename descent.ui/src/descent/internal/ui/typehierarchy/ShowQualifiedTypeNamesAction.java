package descent.internal.ui.typehierarchy;

import org.eclipse.ui.PlatformUI;

import org.eclipse.swt.custom.BusyIndicator;

import org.eclipse.jface.action.Action;

import descent.internal.ui.IJavaHelpContextIds;
import descent.internal.ui.JavaPluginImages;

/**
 * Action enable / disable showing qualified type names
 */
public class ShowQualifiedTypeNamesAction extends Action {

	private TypeHierarchyViewPart fView;	
	
	public ShowQualifiedTypeNamesAction(TypeHierarchyViewPart v, boolean initValue) {
		super(TypeHierarchyMessages.ShowQualifiedTypeNamesAction_label); 
		setDescription(TypeHierarchyMessages.ShowQualifiedTypeNamesAction_description); 
		setToolTipText(TypeHierarchyMessages.ShowQualifiedTypeNamesAction_tooltip); 
		
		JavaPluginImages.setLocalImageDescriptors(this, "th_showqualified.gif"); //$NON-NLS-1$
		
		fView= v;
		setChecked(initValue);
		
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IJavaHelpContextIds.SHOW_QUALIFIED_NAMES_ACTION);
	}

	/*
	 * @see Action#actionPerformed
	 */		
	public void run() {
		BusyIndicator.showWhile(fView.getSite().getShell().getDisplay(), new Runnable() {
			public void run() {
				fView.showQualifiedTypeNames(isChecked());
			}
		});
	}
}
