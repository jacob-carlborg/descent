/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package descent.internal.unittest.ui;


import org.eclipse.core.runtime.CoreException;

import descent.core.IJavaElement;
import descent.core.IJavaProject;

import org.eclipse.swt.widgets.Shell;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;

import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;

import descent.internal.ui.javaeditor.EditorUtility;

/**
 * Abstract Action for opening a Java editor.
 */
public abstract class OpenEditorAction extends Action
{
	private final TestRunnerViewPart fTestRunner;
	
	protected OpenEditorAction(TestRunnerViewPart testRunner)
	{
		super(JUnitMessages.OpenEditorAction_action_label); 
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, getHelpContextId());
		fTestRunner= testRunner;
	}
	
	/*
	 * @see IAction#run()
	 */
	public final void run() {
		ITextEditor textEditor= null;
		try {
			IJavaElement element= findElement();
			if (element == null) {
				MessageDialog.openError(getShell(), 
					JUnitMessages.OpenEditorAction_error_cannotopen_title, JUnitMessages.OpenEditorAction_error_cannotopen_message); 
				return;
			} 
			textEditor= (ITextEditor)EditorUtility.openInEditor(element, true /* always activate */);			
		} catch (CoreException e) {
			ErrorDialog.openError(getShell(), JUnitMessages.OpenEditorAction_error_dialog_title, JUnitMessages.OpenEditorAction_error_dialog_message, e.getStatus()); 
			return;
		}
		if (textEditor == null) {
			fTestRunner.registerInfoMessage(JUnitMessages.OpenEditorAction_message_cannotopen); 
			return;
		}
		reveal(textEditor);
	}

	protected final IJavaProject getLaunchedProject()
	{
		return fTestRunner.getLaunchedProject();
	}
	
	/**
	 * Finds the {@link IJavaElement} to be opened in the editor.
	 * 
	 * @return the element to open or null if the element could not
	 *         be found
	 */
	protected abstract IJavaElement findElement() throws CoreException;
	
	/**
	 * Given an open editor, reveal the relevant data (for example
	 * 
	 * @param editor the editor to reveal the data on, showing the element
	 *               returned by {@link #findElement()}.
	 */
	protected abstract void reveal(ITextEditor editor);
	
	/**
	 * Gets the help contxt ID for this action
	 */
	protected abstract String getHelpContextId();
	
	private final Shell getShell()
	{
		return fTestRunner.getSite().getShell();
	}
}
