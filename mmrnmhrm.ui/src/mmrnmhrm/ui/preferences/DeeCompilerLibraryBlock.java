/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package mmrnmhrm.ui.preferences;


import mmrnmhrm.ui.DeePlugin;

import org.eclipse.dltk.internal.debug.ui.interpreters.AbstractInterpreterLibraryBlock;
import org.eclipse.dltk.internal.debug.ui.interpreters.AddScriptInterpreterDialog;
import org.eclipse.dltk.internal.debug.ui.interpreters.LibraryLabelProvider;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.IBaseLabelProvider;

/**
 * Control used to edit the libraries associated with a Interpreter install
 */
public class DeeCompilerLibraryBlock extends AbstractInterpreterLibraryBlock {

	/**
	 * the prefix for dialog setting pertaining to this block
	 */
	protected static final String DIALOG_SETTINGS_PREFIX = "DeeCompilerLibraryBlock"; //$NON-NLS-1$

	public DeeCompilerLibraryBlock(AddScriptInterpreterDialog d) {
	    super(d);
	}
	@Override
	protected IBaseLabelProvider getLabelProvider() {
		return new LibraryLabelProvider();
	}
	@Override
	protected IDialogSettings getDialogSettions() {
		return DeePlugin.getDefault().getDialogSettings();
		//return RubyDebugUIPlugin.getDefault().getDialogSettings();
	}
}
