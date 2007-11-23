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
package descent.internal.unittest.ui;


import org.eclipse.jface.action.Action;

/**
 * Action to enable/disable stack trace filtering.
 */
public class CompareResultsAction extends Action {

	private FailureTrace fView;	
	
	public CompareResultsAction(FailureTrace view) {
		super(JUnitMessages.CompareResultsAction_label);   
		setDescription(JUnitMessages.CompareResultsAction_description);   
		setToolTipText(JUnitMessages.CompareResultsAction_tooltip);  
		
		setDisabledImageDescriptor(DescentUnittestPlugin.getImageDescriptor("dlcl16/compare.gif"));  //$NON-NLS-1$
		setHoverImageDescriptor(DescentUnittestPlugin.getImageDescriptor("elcl16/compare.gif"));  //$NON-NLS-1$
		setImageDescriptor(DescentUnittestPlugin.getImageDescriptor("elcl16/compare.gif"));  //$NON-NLS-1$
		//PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IJUnitHelpContextIds.ENABLEFILTER_ACTION);
		fView= view;
	}

	/*
	 * @see Action#actionPerformed
	 */		
	public void run() {
		CompareResultDialog dialog= new CompareResultDialog(fView.getShell(), fView.getFailedTest());
		dialog.create();
		dialog.open();
	}
}
