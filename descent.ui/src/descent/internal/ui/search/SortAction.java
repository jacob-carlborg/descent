package descent.internal.ui.search;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.custom.BusyIndicator;


public class SortAction extends Action {
	private int fSortOrder;
	private JavaSearchResultPage fPage;
	
	public SortAction(String label, JavaSearchResultPage page, int sortOrder) {
		super(label);
		fPage= page;
		fSortOrder= sortOrder;
	}

	public void run() {
		BusyIndicator.showWhile(fPage.getViewer().getControl().getDisplay(), new Runnable() {
			public void run() {
				fPage.setSortOrder(fSortOrder);
			}
		});
	}

	public int getSortOrder() {
		return fSortOrder;
	}
}
