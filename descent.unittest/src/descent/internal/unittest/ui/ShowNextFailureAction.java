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

import descent.internal.unittest.DescentUnittestPlugin;

class ShowNextFailureAction extends Action {
	
	private TestRunnerViewPart fPart;

	public ShowNextFailureAction(TestRunnerViewPart part) {
		super(JUnitMessages.ShowNextFailureAction_label);  
		setDisabledImageDescriptor(DescentUnittestPlugin.getImageDescriptor("dlcl16/select_next.gif")); //$NON-NLS-1$
		setHoverImageDescriptor(DescentUnittestPlugin.getImageDescriptor("elcl16/select_next.gif")); //$NON-NLS-1$
		setImageDescriptor(DescentUnittestPlugin.getImageDescriptor("elcl16/select_next.gif")); //$NON-NLS-1$
		setToolTipText(JUnitMessages.ShowNextFailureAction_tooltip); 
		fPart= part;
	}
	
	public void run() {
		fPart.selectNextFailure();
	}
}
