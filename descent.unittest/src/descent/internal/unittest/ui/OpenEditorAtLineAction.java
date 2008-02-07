/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Sebastian Davids: sdavids@gmx.de bug 37333 Failure Trace cannot 
 * 			navigate to non-public class in CU throwing Exception
 *******************************************************************************/
package descent.internal.unittest.ui;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * Open a test in the Java editor and reveal a given line
 */
public class OpenEditorAtLineAction extends OpenModuleAction
{
	private final int fLineNumber;
	
	public OpenEditorAtLineAction(TestRunnerViewPart testRunner,
			String moduleName, int lineNumber)
	{
		super(testRunner, moduleName);
		fLineNumber = lineNumber;
	}
	
	@Override
	protected String getHelpContextId()
	{
		return IJUnitHelpContextIds.OPENEDITORATLINE_ACTION;
	}

	@Override
	protected void reveal(ITextEditor editor)
	{
		if (fLineNumber >= 0)
		{
			try
			{
				IDocument document= editor.getDocumentProvider()
						.getDocument(editor.getEditorInput());
				editor.selectAndReveal(document.getLineOffset(fLineNumber - 1),
						document.getLineLength(fLineNumber - 1));
			}
			catch (BadLocationException x)
			{
				// marker refers to invalid text position -> do nothing
			}
		}
	}
}
