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

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * A composite that lays out children in columns (a fixed num of columns). 
 * Uses GridLayout.
 */
public class ColumnComposite extends DialogComposite {

	public ColumnComposite(Composite parent) {
		this(parent, 1, false);
	}
	
	/** Creates a composite with given numCol columns and no margins */
	public ColumnComposite(Composite parent, int numCol) {
		this(parent, numCol, false);
	}

	/** Creates a composite with given numCol columns and optional margins */
	public ColumnComposite(Composite parent, int numCol, boolean margins) {
		super(parent);
		GridLayout gl = new GridLayout(numCol, false);
		JFaceUtil.initGridLayout(gl, margins, useDialogDefaults ? parent : null);
		setLayout(gl);
	}

}
