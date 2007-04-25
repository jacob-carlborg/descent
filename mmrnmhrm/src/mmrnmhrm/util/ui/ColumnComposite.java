package mmrnmhrm.util.ui;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * A composite that lays out children in columns (a fixed num of columns). 
 * Uses GridLayout.
 */
public class ColumnComposite extends DialogComposite {

	/** Creates a composite with given numCol columns and no margins */
	public ColumnComposite(Composite parent, int numCol) {
		this(parent, numCol, false);
	}

	/** Creates a composite with given numCol columns and optional margins */
	public ColumnComposite(Composite parent, int numCol, boolean margins) {
		super(parent);
		GridLayout gl = new GridLayout(numCol, false);
		initGridLayout(gl, margins, useDialogDefaults ? parent : null);
		setLayout(gl);
	}

}
