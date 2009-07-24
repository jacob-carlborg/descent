package descent.internal.ui.typehierarchy;

import descent.internal.ui.actions.AbstractToggleLinkingAction;


/**
 * This action toggles whether the type hierarchy links its selection to the active
 * editor.
 * 
 * @since 2.1
 */
public class ToggleLinkingAction extends AbstractToggleLinkingAction {
	
	TypeHierarchyViewPart fHierarchyViewPart;
	
	/**
	 * Constructs a new action.
	 */
	public ToggleLinkingAction(TypeHierarchyViewPart part) {
		setChecked(part.isLinkingEnabled());
		fHierarchyViewPart= part;
	}

	/**
	 * Runs the action.
	 */
	public void run() {
		fHierarchyViewPart.setLinkingEnabled(isChecked());
	}

}
