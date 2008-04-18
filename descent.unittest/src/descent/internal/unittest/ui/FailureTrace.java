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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import descent.unittest.ITestResult;

/**
 * A pane that shows a stack trace of a failed test.
 */
public class FailureTrace implements IMenuListener
{
	private static final Pattern TRACE_LINE_PATTERN = Pattern.compile(
			"^<([^\\:]*):(\\d*)>"); //$NON-NLS-1$
    
	private Table fTable;
	private TestRunnerViewPart fTestRunner;
	private ITestResult fInputResult;
	private final Clipboard fClipboard;
    private TestElement fFailure;
	private final FailureTableDisplay fFailureTableDisplay;
	private final TableTraceWriter fTableTraceWriter;

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
		fTableTraceWriter = new TableTraceWriter(fFailureTableDisplay);
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
		Matcher m = TRACE_LINE_PATTERN.matcher(traceLine);
		if(m.find())
		{
			try
			{
				String module = m.group(1);
				int line = Integer.valueOf(m.group(2));
				
				return new OpenEditorAtLineAction(fTestRunner, module, line);
			}
			
			// If any of these exceptions are thrown, fall through & return null
			catch(IndexOutOfBoundsException e) { }
			catch(NumberFormatException e) { }
		}
		
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
		TraceUtil.writeTrace(result, fTableTraceWriter);
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
    
    public Shell getShell() {
        return fTable.getShell();
    }

	public FailureTableDisplay getFailureTableDisplay() {
		return fFailureTableDisplay;
	}
}
