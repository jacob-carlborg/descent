package descent.internal.ui.search;

import org.eclipse.jface.action.Action;


public class GroupAction extends Action {
	private int fGrouping;
	private JavaSearchResultPage fPage;
	
	public GroupAction(String label, String tooltip, JavaSearchResultPage page, int grouping) {
		super(label);
		setToolTipText(tooltip);
		fPage= page;
		fGrouping= grouping;
	}

	public void run() {
		fPage.setGrouping(fGrouping);
	}

	public int getGrouping() {
		return fGrouping;
	}
}
