package descent.internal.ui.workingsets;

import org.eclipse.jface.action.IMenuManager;

public interface IWorkingSetActionGroup {

	public static final String ACTION_GROUP= "working_set_action_group"; //$NON-NLS-1$
	
	public void fillViewMenu(IMenuManager mm);
	
	public void cleanViewMenu(IMenuManager menuManager);

}
