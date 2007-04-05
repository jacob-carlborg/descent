package mmrnmhrm.ui.util;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

/**
 * A SelectionListiner that only responds to normal selection events
 * (widgetSelected).
 */
public abstract class SimpleSelectionListener extends Object implements
		SelectionListener {

	public void widgetDefaultSelected(SelectionEvent e) {
		// Do nothing
	}

	public abstract void widgetSelected(SelectionEvent e) ;

}
