/**
 * 
 */
package mmrnmhrm.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class DeeOpenSearchPageAction implements IWorkbenchWindowActionDelegate {

	private static final String DEE_SEARCH_PAGE_ID = "mmrnmhrm.ui.DeeSearchPage";

	private IWorkbenchWindow window;

	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

	public void run(IAction action) {
		if (window == null || window.getActivePage() == null) {
			DeeOpenSearchPageHandler.beep();
			return;
		}

		NewSearchUI.openSearchDialog(window, DEE_SEARCH_PAGE_ID);
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

	public void dispose() {
		window = null;
	}

}