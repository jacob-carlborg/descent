/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Sebastian Davids: sdavids@gmx.de bug 37333, 26653 
 *     Johan Walles: walles@mailblocks.com bug 68737
 *******************************************************************************/
package descent.internal.unittest.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.util.IOpenEventListener;
import org.eclipse.jface.util.OpenStrategy;

import descent.internal.unittest.model.TestCaseElement;
import descent.internal.unittest.model.TestElement;
import descent.internal.unittest.ui.ITraceDisplay.LineType;
import descent.unittest.IStackTraceElement;
import descent.unittest.ITestResult;

/**
 * A pane that shows a stack trace of a failed test.
 */
public class FailureTrace implements IMenuListener
{	
	/**
	 * Was TextualTrace, but considering that there's no text involved and this
	 * is quite a bit simpler, I thought an inner class would be fine.
	 */
	private static class TraceDisplayHandler
	{
		private static final int MAX_LABEL_LENGTH = 256;
		
		final ITestResult result;
		
		TraceDisplayHandler(ITestResult result)
		{
			this.result = result;
		}
		
		public void display(ITraceDisplay table)
		{
			String message = result.getMessage();
			switch(result.getResultType())
			{
				case PASSED:
					// I'm guesing this won't ever happen, but just in case
					return;
				case FAILED:
					displayWrappedLine(table, String.format(
							"%1$s[%2$s:%3$d]",
							message != null ? message + " " : "",
							result.getFile(),
							result.getLine()),
							LineType.EXCEPTION);
					break;
				case ERROR:
					displayWrappedLine(table, String.format(
							"%1$s%2$s",
							result.getExceptionType(),
							message != null ? ": " + message : ""),
							LineType.EXCEPTION);
			}
			IStackTraceElement[] stackTrace = result.getStackTrace();
			if(null != stackTrace && stackTrace.length > 0)
				displayStackTrace(table, stackTrace);
		}
		
		private static void displayStackTrace(ITraceDisplay table,
				IStackTraceElement[] stackTrace)
		{
			for(IStackTraceElement ste : stackTrace)
			{
				if(ste.lineInfoFound())
				{
					displayWrappedLine(table, String.format(
							"%1$s [%2$s:%3$d]",
							ste.getFunction(),
							ste.getFile(),
							ste.getLine()),
							LineType.STACK_FRAME);
				}
				else
				{
					displayWrappedLine(table, String.format(
							"%1$s [0x%2$x]",
							ste.getFunction(),
							ste.getAddress()),
							LineType.STACK_FRAME);
				}
			}
		}
		
		private static void displayWrappedLine(ITraceDisplay table, String line,
				LineType type) {
			final int labelLength = line.length();
			if (labelLength < MAX_LABEL_LENGTH) {
				table.addTraceLine(type, line);
			} else {
				// workaround for bug 74647: JUnit view truncates
				// failure message
				table.addTraceLine(type, line.substring(0, MAX_LABEL_LENGTH));
				int offset = MAX_LABEL_LENGTH;
				while (offset < labelLength) {
					int nextOffset = Math.min(labelLength, offset + MAX_LABEL_LENGTH);
					table.addTraceLine(LineType.NORMAL, line.substring(offset,
							nextOffset));
					offset = nextOffset;
				}
			}
		}
	}
    
    static final String FRAME_PREFIX= "at "; //$NON-NLS-1$
	private Table fTable;
	private TestRunnerViewPart fTestRunner;
	private ITestResult fInputResult;
	private final Clipboard fClipboard;
    private TestElement fFailure;
	private final FailureTableDisplay fFailureTableDisplay;

	public FailureTrace(Composite parent, Clipboard clipboard, TestRunnerViewPart testRunner, ToolBar toolBar) {
		assert(null != clipboard);
		
		// fill the failure trace viewer toolbar
		ToolBarManager failureToolBarmanager= new ToolBarManager(toolBar);		
		failureToolBarmanager.update(true);
		
		fTable= new Table(parent, SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL);
		fTestRunner= testRunner;
		fClipboard= clipboard;
		
		OpenStrategy handler = new OpenStrategy(fTable);
		handler.addOpenListener(new IOpenEventListener() {
			public void handleOpen(SelectionEvent e) {
				if (fTable.getSelection().length != 0) {
					Action a = createOpenEditorAction(getSelectedText());
					if (a != null)
						a.run();
				}
			}
		});
		
		initMenu();
		
		fFailureTableDisplay = new FailureTableDisplay(fTable);
	}
	
	private void initMenu() {
		MenuManager menuMgr= new MenuManager();
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(this);
		Menu menu= menuMgr.createContextMenu(fTable);
		fTable.setMenu(menu);		
	}
	
	public void menuAboutToShow(IMenuManager manager) {
		if (fTable.getSelectionCount() > 0) {
			Action a= createOpenEditorAction(getSelectedText());
			if (a != null)
				manager.add(a);		
			manager.add(new JUnitCopyAction(FailureTrace.this, fClipboard));
		}
	}

	public ITestResult getTestResult() {
		return fInputResult;
	}
	
	private String getSelectedText() {
		return fTable.getSelection()[0].getText();
	}				

	private Action createOpenEditorAction(String traceLine)
	{
		// TODO
		return null;
	}
	
	/**
	 * Returns the composite used to present the trace
	 * @return The composite
	 */
	Composite getComposite(){
		return fTable;
	}
	
	/**
	 * Refresh the table from the trace.
	 */
	public void refresh() {
		updateTable(fInputResult);
	}
	
	/**
	 * Shows a TestFailure
	 * @param test the failed test
	 */
	public void showFailure(TestElement test)
	{	
	    fFailure = test;
	    ITestResult result = null;
	    
	    if (test != null && test instanceof TestCaseElement) 
	    {
	    	TestCaseElement tce = (TestCaseElement) test;
	    	if(tce.getStatus().isErrorOrFailure())
	    		result = tce.getResult();
	    }
	    
		if (fInputResult == result)
			return;
		
		fInputResult = result;
		updateTable(result);
	}

	private void updateTable(ITestResult result) {
		if(result == null) { //$NON-NLS-1$
			clear();
			return;
		}
		
		fTable.setRedraw(false);
		fTable.removeAll();
		(new TraceDisplayHandler(result)).display(fFailureTableDisplay);
		fTable.setRedraw(true);
	}

	/**
	 * Shows other information than a stack trace.
	 * @param text the informational message to be shown
	 */
	public void setInformation(String text) {
		clear();
		TableItem tableItem= fFailureTableDisplay.newTableItem();
		tableItem.setText(text);
	}

	/**
	 * Clears the non-stack trace info
	 */
	public void clear() {
		fTable.removeAll();
		fInputResult = null;
	}

    public TestElement getFailedTest() {
        return fFailure;
    }
    
    public String getTraceAsString()
	{
		return ""; // TODO
	}
    
    public Shell getShell() {
        return fTable.getShell();
    }

	public FailureTableDisplay getFailureTableDisplay() {
		return fFailureTableDisplay;
	}
}
