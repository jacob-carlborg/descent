package descent.internal.ui.preferences;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;

public class DProjectProperties extends PropertyPage implements
		IWorkbenchPropertyPage {

	public DProjectProperties() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Control createContents(Composite parent) {
		// TODO Auto-generated method stub
		Label l = new Label(parent,SWT.SHADOW_ETCHED_IN);
		l.setText("Hey biatch!");
		return null;
	}

}
