/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.ui.editor.text;

import mmrnmhrm.ui.actions.GoToDefinitionHandler;
import mmrnmhrm.ui.actions.ISimpleRunnable;
import mmrnmhrm.ui.actions.OperationsManager;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.texteditor.ITextEditor;


/**
 * Lang element hyperlink.
 */
public class DeeElementHyperlink implements IHyperlink {

	private final IRegion fRegion;
	private final ITextEditor fTextEditor;
	private int offset;

	/**
	 * Creates a new Lang element hyperlink.
	 * @param i 
	 */
	public DeeElementHyperlink(int offset, IRegion region, ITextEditor textEditor) {
		Assert.isNotNull(textEditor);
		Assert.isNotNull(region);

		this.offset = offset;
		fRegion= region;
		fTextEditor= textEditor;
	}

	public IRegion getHyperlinkRegion() {
		return fRegion;
	}

	public void open() {
		OperationsManager.executeOperation("Open Element", new ISimpleRunnable() {
			public void run() throws CoreException {
				GoToDefinitionHandler.executeOperation(fTextEditor, true, offset);
			}
		});
	}

	public String getTypeLabel() {
		return null;
	}

	public String getHyperlinkText() {
		return null;
	}
}
