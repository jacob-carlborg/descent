package descent.internal.debug.ui.actions;

import org.eclipse.swt.accessibility.AccessibleAdapter;
import org.eclipse.swt.accessibility.AccessibleEvent;
import org.eclipse.swt.widgets.Control;

public class ControlAccessibleListener extends AccessibleAdapter {
	private String controlName;
	
	public ControlAccessibleListener(String name) {
		controlName = name;
	}

	public void getName(AccessibleEvent e) {
		e.result = controlName;
	}

	public static void addListener(Control comp, String name) {
		//strip mnemonic
		String[] strs = name.split("&"); //$NON-NLS-1$
		StringBuffer stripped = new StringBuffer();
		for (int i = 0; i < strs.length; i++) {
			stripped.append(strs[i]);
		}
		comp.getAccessible().addAccessibleListener(new ControlAccessibleListener(stripped.toString()));
	}
}
