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
package descent.internal.ui.refactoring;

import org.eclipse.core.runtime.CoreException;

import org.eclipse.swt.widgets.Shell;

import descent.internal.ui.refactoring.actions.RefactoringStarter;

import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;

/**
 * Opens the user interface for a given refactoring.
 */
public class UserInterfaceStarter {
	
	private RefactoringWizard fWizard;
	
	/**
	 * Initializes this user interface starter with the given
	 * wizard.
	 * 
	 * @param wizard the refactoring wizard to use
	 */
	public void initialize(RefactoringWizard wizard) {
		fWizard= wizard;		
	}
	
	/**
	 * Actually activates the user interface. This default implementation
	 * assumes that the configuration element passed to <code>initialize
	 * </code> has an attribute wizard denoting the wizard class to be
	 * used for the given refactoring.
	 * <p>
	 * Subclasses may override to open a different user interface
	 * 
	 * @param refactoring the refactoring for which the user interface
	 *  should be opened
	 * @param parent the parent shell to be used
	 * @param mustSaveEditors <code>true</code> iff dirty editors must be
	 *  saved before the refactoring is started, <code>false</code> otherwise
	 * 
	 * @exception CoreException if the user interface can't be activated
	 */
	public void activate(Refactoring refactoring, Shell parent, boolean mustSaveEditors) throws CoreException {
		new RefactoringStarter().activate(refactoring, fWizard, parent, fWizard.getDefaultPageTitle(), mustSaveEditors);
	}
}
