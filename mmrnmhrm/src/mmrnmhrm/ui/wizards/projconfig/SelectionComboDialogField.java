package mmrnmhrm.ui.wizards.projconfig;

import org.eclipse.jdt.internal.ui.wizards.dialogfields.ComboDialogField;
import org.eclipse.swt.SWT;

public class SelectionComboDialogField extends ComboDialogField {
	
	Object[] fObjectItems;

	public SelectionComboDialogField() {
		super(SWT.NONE);
	}

	
	public void setObjectItems(Object[] items) {
		fObjectItems = items;
		String[] strArray = new String[items.length];
		for(int i = 0; i < items.length; i++ ) {
			strArray[i] = items[i].toString();
		}
		super.setItems(strArray);
	}
	
	public Object getSelectedObject() {
		return fObjectItems[super.getSelectionIndex()];
	}
}
