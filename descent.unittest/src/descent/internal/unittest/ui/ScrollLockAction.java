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
import org.eclipse.ui.PlatformUI;

import descent.internal.unittest.DescentUnittestPlugin;

/**
 * Toggles console auto-scroll
 */
public class ScrollLockAction extends Action {

	private TestRunnerViewPart fRunnerViewPart;

	public ScrollLockAction(TestRunnerViewPart viewer) {
		super(JUnitMessages.ScrollLockAction_action_label); 
		fRunnerViewPart= viewer;
		setToolTipText(JUnitMessages.ScrollLockAction_action_tooltip); 
		setDisabledImageDescriptor(DescentUnittestPlugin.getImageDescriptor("dlcl16/lock.gif")); //$NON-NLS-1$
		setHoverImageDescriptor(DescentUnittestPlugin.getImageDescriptor("elcl16/lock.gif")); //$NON-NLS-1$
		setImageDescriptor(DescentUnittestPlugin.getImageDescriptor("elcl16/lock.gif")); //$NON-NLS-1$
		PlatformUI.getWorkbench().getHelpSystem().setHelp(
			this,
			IJUnitHelpContextIds.OUTPUT_SCROLL_LOCK_ACTION);
		setChecked(false);
	}

	/**
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	public void run() {
		fRunnerViewPart.setAutoScroll(!isChecked());
	}
}

