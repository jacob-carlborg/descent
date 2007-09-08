package melnorme.util.ui.swt;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * A composite that lays out children in rows. 
 * Uses GridLayout.
 */
public class RowComposite extends DialogComposite {

	/** Creates a 1 column composite with no margins and SWT default spacing. */
	public RowComposite(Composite parent) {
		this(parent, false);
	}

	/** Creates a 1 column composite with optional margins and default spacing. */
	public RowComposite(Composite parent, boolean margins) {
		super(parent);
		GridLayout gl = new GridLayout();
		SWTUtilExt.initGridLayout(gl, margins, useDialogDefaults ? parent : null);
		setLayout(gl);
	}

 }
