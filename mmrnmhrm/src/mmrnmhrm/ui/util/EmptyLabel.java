package mmrnmhrm.ui.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class EmptyLabel {
	
	private Label label;

	public EmptyLabel(Composite parent) {
		label = new Label(parent, SWT.NONE);
		label.setText(" ");
	}
}
