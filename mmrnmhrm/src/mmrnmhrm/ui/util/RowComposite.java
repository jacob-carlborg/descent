package mmrnmhrm.ui.util;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * A composite that lays out children in rows. 
 * Uses GridLayout.
 */
public class RowComposite extends DialogComposite {

	public RowComposite(Composite parent) {
		this(parent, false);
	}

	public RowComposite(Composite parent, boolean margins) {
		super(parent);
		GridLayout gl = new GridLayout();
		initGridLayout(gl, margins, useDialogDefaults ? parent : null);
		setLayout(gl);
	}

 }
