package descent.internal.ui.search;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;


public class FilterAction extends Action {
	private MatchFilter fFilter;
	private JavaSearchResultPage fPage;
	
	public FilterAction(JavaSearchResultPage page, MatchFilter filter) {
		super(filter.getActionLabel(), IAction.AS_CHECK_BOX);
		fPage= page;
		fFilter= filter;
	}

	public void run() {
		if (fPage.hasMatchFilter(getFilter())) {
			fPage.removeMatchFilter(fFilter);
		} else {
			fPage.addMatchFilter(fFilter);
		}
	}

	public MatchFilter getFilter() {
		return fFilter;
	}

	public void updateCheckState() {
		setChecked(fPage.hasMatchFilter(getFilter()));
	}
}
