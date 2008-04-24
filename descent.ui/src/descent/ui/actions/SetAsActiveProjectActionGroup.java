package descent.ui.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.part.Page;

import descent.ui.IContextMenuConstants;

public class SetAsActiveProjectActionGroup extends ActionGroup {
	
	private String fGroupName= IContextMenuConstants.GROUP_BUILD;
	
	private IWorkbenchSite fSite;
    private List/*<Action>*/ fActions;
	
	public SetAsActiveProjectActionGroup(Page page) {
        this(page.getSite());
    }
	
    public SetAsActiveProjectActionGroup(IViewPart part) {
        this(part.getSite());
    }
    
    private SetAsActiveProjectActionGroup(IWorkbenchSite site) {
        fSite= site;
        fActions= new ArrayList();
        
		final SetAsActiveProjectAction setAsActiveProjectAction= new SetAsActiveProjectAction();
		fActions.add(setAsActiveProjectAction);
		
		final ISelectionProvider provider= fSite.getSelectionProvider();
		for (Iterator iter= fActions.iterator(); iter.hasNext();) {
			Action action= (Action)iter.next();
			if (action instanceof ISelectionChangedListener) {
				provider.addSelectionChangedListener((ISelectionChangedListener)action);
			}
		}	
    }
    
    @Override
    public void fillContextMenu(IMenuManager menu) {
    	super.fillContextMenu(menu);
        menu.appendToGroup(fGroupName, (IAction) fActions.get(0));
    }

}
