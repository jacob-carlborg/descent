package mmrnmhrm.ui.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * A Composite which assumes some default layout and layoutdata settings. 
 */
public class DialogComposite extends Composite implements IGridLayoutComposite {

	/** Controls whether to use dialog default margins or SWT default margins. */
	protected static final boolean useDialogDefaults = false;
	
	/** Creates a composite with no margins and SWT default spacing. */
	public DialogComposite(Composite parent) {
		this(parent, false, false);
	}

	/** Creates a composite with optional margins and default spacing. */
	public DialogComposite(Composite parent, boolean margins) {
		this(parent, margins, false);
	}

	/** Creates a composite with optional margins, initialized to either
	 * SWT defaults or dialog units defaults. */
	public DialogComposite(Composite parent, boolean margins, boolean useDLUdefaults) {
		super(parent, SWT.NONE);

		SWTUtil2.setRandomColor(this);
		
		if(parent instanceof IGridLayoutComposite) {
			setLayoutData(new GridData());
		}
	}

	
	protected void initGridLayout(GridLayout gl, boolean margins, Control testControl) {
		if(testControl != null)
			SWTUtil2.initGridLayout(gl, testControl);

		if(!margins) {
			gl.marginWidth = 0;
			gl.marginHeight = 0;
			
		}
	}

	public void recursiveSetEnabled(boolean enabled) {
		super.setEnabled(enabled);
		SWTUtil2.recursiveSetEnabled(this, enabled);
	}
}