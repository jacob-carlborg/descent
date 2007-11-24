package descent.internal.ui.search;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.window.Window;


public class FiltersDialogAction extends Action {
	private JavaSearchResultPage fPage;
	
	public FiltersDialogAction(JavaSearchResultPage page) {
		super(SearchMessages.FiltersDialogAction_label); 
		fPage= page;
	}

	public void run() {
		FiltersDialog dialog = new FiltersDialog(fPage);

		if (dialog.open() == Window.OK) {
			fPage.setFilters(dialog.getEnabledFilters());
			fPage.enableLimit(dialog.isLimitEnabled());
			fPage.setElementLimit(dialog.getElementLimit());
		}
	}

}
