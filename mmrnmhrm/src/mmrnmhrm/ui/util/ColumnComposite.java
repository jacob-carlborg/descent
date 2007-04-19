package mmrnmhrm.ui.util;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * A composite that lays out children in columns (a fixed num of columns). 
 * Uses GridLayout.
 */
public class ColumnComposite extends DialogComposite implements IGridLayoutControl {


	public ColumnComposite(Composite parent, int numCol) {
		super(parent);
		GridLayout gl = new GridLayout(numCol, false);
		gl.marginWidth = 0;
		gl.marginHeight = 0;
	    setLayout(gl);
		SWTDebug.setRandomColor(this);
	}

}