package mmrnmhrm.ui.util;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * A composite that lays out children in rows. 
 * Uses GridLayout.
 */
public class RowComposite extends DialogComposite implements IGridLayoutControl {

	public RowComposite(Composite parent) {
		super(parent);
		GridLayout gl = new GridLayout();
		gl.marginWidth = 0;
		gl.marginHeight = 0;
	    setLayout(gl);
		SWTDebug.setRandomColor(this);
		
		if(parent instanceof IGridLayoutControl) {
			setLayoutData(new GridData());
		}
	}

}
