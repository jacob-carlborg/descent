package descent.internal.ui.workingsets;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.util.Assert;

import org.eclipse.ui.PlatformUI;

import descent.internal.ui.IJavaHelpContextIds;

/**
 * Clears the selected working set in the action group's view.
 * 
 * @since 2.0
 */
public class ClearWorkingSetAction extends Action {
	
	private WorkingSetFilterActionGroup fActionGroup;

	public ClearWorkingSetAction(WorkingSetFilterActionGroup actionGroup) {
		super(WorkingSetMessages.ClearWorkingSetAction_text); 
		Assert.isNotNull(actionGroup);
		setToolTipText(WorkingSetMessages.ClearWorkingSetAction_toolTip); 
		setEnabled(actionGroup.getWorkingSet() != null);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IJavaHelpContextIds.CLEAR_WORKING_SET_ACTION);
		fActionGroup= actionGroup;
	}

	/*
	 * Overrides method from Action
	 */
	public void run() {
		fActionGroup.setWorkingSet(null, true);
	}
}
