package descent.internal.ui.typehierarchy;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.util.Assert;

import org.eclipse.ui.PlatformUI;

import descent.internal.ui.IJavaHelpContextIds;
import descent.internal.ui.JavaPluginImages;

/**
 * Toggles the orientationof the layout of the type hierarchy
 */
public class ToggleOrientationAction extends Action {

	private TypeHierarchyViewPart fView;	
	private int fActionOrientation;
	
	public ToggleOrientationAction(TypeHierarchyViewPart v, int orientation) {
		super("", AS_RADIO_BUTTON); //$NON-NLS-1$
		if (orientation == TypeHierarchyViewPart.VIEW_ORIENTATION_HORIZONTAL) {
			setText(TypeHierarchyMessages.ToggleOrientationAction_horizontal_label); 
			setDescription(TypeHierarchyMessages.ToggleOrientationAction_horizontal_description); 
			setToolTipText(TypeHierarchyMessages.ToggleOrientationAction_horizontal_tooltip); 
			JavaPluginImages.setLocalImageDescriptors(this, "th_horizontal.gif"); //$NON-NLS-1$
		} else if (orientation == TypeHierarchyViewPart.VIEW_ORIENTATION_VERTICAL) {
			setText(TypeHierarchyMessages.ToggleOrientationAction_vertical_label); 
			setDescription(TypeHierarchyMessages.ToggleOrientationAction_vertical_description); 
			setToolTipText(TypeHierarchyMessages.ToggleOrientationAction_vertical_tooltip); 
			JavaPluginImages.setLocalImageDescriptors(this, "th_vertical.gif"); //$NON-NLS-1$
		} else if (orientation == TypeHierarchyViewPart.VIEW_ORIENTATION_AUTOMATIC) {
			setText(TypeHierarchyMessages.ToggleOrientationAction_automatic_label); 
			setDescription(TypeHierarchyMessages.ToggleOrientationAction_automatic_description); 
			setToolTipText(TypeHierarchyMessages.ToggleOrientationAction_automatic_tooltip); 
			JavaPluginImages.setLocalImageDescriptors(this, "th_automatic.gif"); //$NON-NLS-1$
		} else if (orientation == TypeHierarchyViewPart.VIEW_ORIENTATION_SINGLE) {
			setText(TypeHierarchyMessages.ToggleOrientationAction_single_label); 
			setDescription(TypeHierarchyMessages.ToggleOrientationAction_single_description); 
			setToolTipText(TypeHierarchyMessages.ToggleOrientationAction_single_tooltip); 
			JavaPluginImages.setLocalImageDescriptors(this, "th_single.gif"); //$NON-NLS-1$
		} else {
			Assert.isTrue(false);
		}
		fView= v;
		fActionOrientation= orientation;
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IJavaHelpContextIds.TOGGLE_ORIENTATION_ACTION);
	}
	
	public int getOrientation() {
		return fActionOrientation;
	}	
	
	/*
	 * @see Action#actionPerformed
	 */		
	public void run() {
		if (isChecked()) {
			fView.fOrientation= fActionOrientation; 
			fView.computeOrientation();
		}
	}
	
}
