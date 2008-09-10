/*******************************************************************************
 * Copyright (c) 2007 DSource.org and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial implementation
 *******************************************************************************/
package melnorme.util.ui.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * A Composite which assumes some default layout and layoutdata settings. 
 */
public class DialogComposite extends Composite {

	/** Controls whether to use dialog default margins or SWT default margins. */
	protected static final boolean useDialogDefaults = false;
	
	/** Creates a composite with no margins and SWT default spacing. */
	public DialogComposite(Composite parent) {
		super(parent, SWT.NONE);
		
		SWTUtilExt.setRandomDebugColor(this);
		
		if(parent.getLayout() instanceof GridLayout) {
			setLayoutData(SWTLayoutUtil.createDefaultGridData());
		}
	}

	/** Sets the enable state of this control and of all children. */
	public void recursiveSetEnabled(boolean enabled) {
		super.setEnabled(enabled);
		SWTUtilExt.recursiveSetEnabled(this, enabled);
	}
}