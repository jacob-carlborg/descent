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
package descent.internal.ui.wizards;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import descent.core.IJavaElement;

import descent.ui.wizards.NewPackageWizardPage;

import descent.internal.ui.JavaPlugin;
import descent.internal.ui.JavaPluginImages;

public class NewPackageCreationWizard extends NewElementWizard {

	private NewPackageWizardPage fPage;

	public NewPackageCreationWizard() {
		super();
		setDefaultPageImageDescriptor(JavaPluginImages.DESC_WIZBAN_NEWPACK);
		setDialogSettings(JavaPlugin.getDefault().getDialogSettings());
		setWindowTitle(NewWizardMessages.NewPackageCreationWizard_title); 
	}

	/*
	 * @see Wizard#addPages
	 */	
	public void addPages() {
		super.addPages();
		fPage= new NewPackageWizardPage();
		addPage(fPage);
		fPage.init(getSelection());
	}	
	
	/* (non-Javadoc)
	 * @see descent.internal.ui.wizards.NewElementWizard#finishPage(org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected void finishPage(IProgressMonitor monitor) throws InterruptedException, CoreException {
		fPage.createPackage(monitor); // use the full progress monitor
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.IWizard#performFinish()
	 */
	public boolean performFinish() {
		boolean res= super.performFinish();
		if (res) {
			selectAndReveal(fPage.getModifiedResource());
		}
		return res;
	}
	
	/* (non-Javadoc)
	 * @see descent.internal.ui.wizards.NewElementWizard#getCreatedElement()
	 */
	public IJavaElement getCreatedElement() {
		return fPage.getNewPackageFragment();
	}	
	
}
