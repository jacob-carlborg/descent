package mmrnmhrm.ui.actions;

import mmrnmhrm.ui.DeePlugin;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class DeeOpenSearchPageAction implements IWorkbenchWindowActionDelegate {
	
	private static final String DEE_SEARCH_PAGE_ID = DeePlugin.EXTENSIONS_IDPREFIX+"DeeSearchPage";
	
	private IWorkbenchWindow window;
	
	@Override
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}
	
	@Override
	public void run(IAction action) {
		if (window == null || window.getActivePage() == null) {
			DeeOpenSearchPageHandler.beep();
			return;
		}
		
		NewSearchUI.openSearchDialog(window, DEE_SEARCH_PAGE_ID);
	}
	
	@Override
	public void selectionChanged(IAction action, ISelection selection) {
	}
	
	@Override
	public void dispose() {
		window = null;
	}
	
}