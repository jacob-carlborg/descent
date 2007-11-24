package descent.internal.ui.search;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.util.Assert;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.actions.ActionGroup;

import descent.internal.ui.actions.CompositeActionGroup;
import descent.ui.actions.GenerateActionGroup;
import descent.ui.actions.JavaSearchActionGroup;
import descent.ui.actions.NavigateActionGroup;

class NewSearchViewActionGroup extends CompositeActionGroup {
	NavigateActionGroup fNavigateActionGroup;
	
	public NewSearchViewActionGroup(IViewPart part) {
		Assert.isNotNull(part);
		setGroups(new ActionGroup[]{
			fNavigateActionGroup= new NavigateActionGroup(part),
			new GenerateActionGroup(part), 
			/* TODO JDT UI refactor 
			new RefactorActionGroup(part),
			*/
			new JavaSearchActionGroup(part) 
			});
	}
	
	public void handleOpen(OpenEvent event) {
		IAction openAction= fNavigateActionGroup.getOpenAction();
		if (openAction != null && openAction.isEnabled()) {
			openAction.run();
			return;
		}
	}
}

