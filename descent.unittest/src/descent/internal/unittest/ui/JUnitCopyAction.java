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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import org.eclipse.swt.SWTError;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.SelectionListenerAction;

import descent.internal.unittest.model.TestCaseElement;
import descent.internal.unittest.model.TestElement;
import descent.unittest.ITestResult;

import org.eclipse.jface.dialogs.MessageDialog;

/**
 * Copies a test failure stack trace to the clipboard.
 */
public class JUnitCopyAction extends SelectionListenerAction
{
	private final FailureTrace fView;	
	private final Clipboard fClipboard;
	private TestElement fTestElement;

	/**
	 * Constructor for CopyTraceAction.
	 * @param view 
	 * @param clipboard 
	 */
	public JUnitCopyAction(FailureTrace view, Clipboard clipboard) {
		super(JUnitMessages.CopyTrace_action_label);  
		assert(null != clipboard);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IJUnitHelpContextIds.COPYTRACE_ACTION);
		fView= view;
		fClipboard= clipboard;
	}

	/*
	 * @see IAction#run()
	 */
	public void run()
	{
		if(null == fTestElement || !(fTestElement instanceof TestCaseElement))
			return;
		
		TestCaseElement testCaseElement = (TestCaseElement) fTestElement;
		ITestResult result = testCaseElement.getResult();
		String source = TraceUtil.getTraceAsString(result);
		
		TextTransfer plainTextTransfer = TextTransfer.getInstance();
		try{
			fClipboard.setContents(
				new String[]{ convertLineTerminators(source) }, 
				new Transfer[]{ plainTextTransfer });
		}  catch (SWTError e){
			if (e.code != DND.ERROR_CANNOT_SET_CLIPBOARD) 
				throw e;
			if (MessageDialog.openQuestion(fView.getComposite().getShell(), JUnitMessages.CopyTraceAction_problem, JUnitMessages.CopyTraceAction_clipboard_busy))  
				run();
		}
	}


	public void handleTestSelected(TestElement test) {
		fTestElement= test;
	}
	
	private String convertLineTerminators(String in) {
		StringWriter stringWriter= new StringWriter();
		PrintWriter printWriter= new PrintWriter(stringWriter);
		StringReader stringReader= new StringReader(in);
		BufferedReader bufferedReader= new BufferedReader(stringReader);		
		String line;
		try {
			while ((line= bufferedReader.readLine()) != null) {
				printWriter.println(line);
			}
		} catch (IOException e) {
			return in; // return the trace unfiltered
		}
		return stringWriter.toString();
	}
}
