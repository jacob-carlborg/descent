package mmrnmhrm.ui.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class DialogComposite extends Composite {

	public DialogComposite(Composite parent) {
		super(parent, SWT.NONE);
	}

	public void setRecursiveEnabled(boolean enabled) {
		super.setEnabled(enabled);
		recursiveSetEnabled(this, enabled);
	}
	
	public static void recursiveSetEnabled(Composite composite, boolean enabled) {
		for(Control control : composite.getChildren() ) {
			if(control instanceof Composite) {
				recursiveSetEnabled((Composite) control, enabled);
			}
			control.setEnabled(enabled);
		}
	}

}