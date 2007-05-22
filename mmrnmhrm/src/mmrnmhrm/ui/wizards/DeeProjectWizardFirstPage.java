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
package mmrnmhrm.ui.wizards;


import java.text.MessageFormat;
import java.util.Observable;
import java.util.Observer;

import melnorme.util.ui.LayoutUtil;
import melnorme.util.ui.SWTUtil2;

import org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.SelectionButtonDialogField;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Link;


/**
 * The first page of the New Project Wizard for D.
 */
public class DeeProjectWizardFirstPage extends LangProjectWizardFirstPage {
	
	private final class DCEGroup implements Observer {

		private final SelectionButtonDialogField fUseDefaultDCE;
		private final Group fGroup;
		private final Link fPreferenceLink;
		
		public DCEGroup(Composite composite) {
			fGroup= new Group(composite, SWT.NONE);
			fGroup.setLayoutData(LayoutUtil.createDefaultGridData());
			fGroup.setLayout(SWTUtil2.createGridLayout(3, null));
			fGroup.setText(DeeNewWizardMessages.LangNewProject_Page1_DCEGroup_title); 
						
			fUseDefaultDCE= new SelectionButtonDialogField(SWT.RADIO);
			fUseDefaultDCE.setLabelText(getDefaultJVMLabel());
			fUseDefaultDCE.doFillIntoGrid(fGroup, 2);
			
			fPreferenceLink= new Link(fGroup, SWT.NONE);
			fPreferenceLink.setFont(fGroup.getFont());
			fPreferenceLink.setText(DeeNewWizardMessages.LangNewProject_Page1_DCEGroup_link_description);
			fPreferenceLink.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
			fPreferenceLink.setVisible(false);
		
			DialogField.createEmptySpace(fGroup);
			
			fUseDefaultDCE.setSelection(true);
		}


		private String getDefaultJVMName() {
			return "DMD1.---";
		}

		private String getDefaultJVMLabel() {
			return MessageFormat.format(DeeNewWizardMessages.LangNewProject_Page1_DCEGroup_default_compliance, getDefaultJVMName());
		}

		public void update(Observable o, Object arg) {
			//updateEnableState(fDetectGroup.mustDetect());
		}

	}

	public static final String PAGE_NAME = "DeeProjectWizardPage1";

	private DCEGroup fDCEGroup;


	/**
	 * Create a new <code>SimpleProjectFirstPage</code>.
	 */
	public DeeProjectWizardFirstPage() {
		super(PAGE_NAME);
		setTitle(DeeNewWizardMessages.LangNewProject_Page1_pageTitle); 
		setDescription(DeeNewWizardMessages.LangNewProject_Page1_pageDescription); 
	}
	
	protected Observer[] createCustomControls(Composite content) {
		fDCEGroup= new DCEGroup(content);

		return new Observer[] { fDCEGroup };
	}
	
	
	public DeeProjectWizard getWizard() {
		return (DeeProjectWizard) super.getWizard();
	}
	
	/* XXX: Unclean hack: getNextPage used to prevent page advancement on errors.
	 * This means getNextPage should only be called by nextPressed() and not by
	 * others like canFlipToNextPage().*/ 
	@Override
	public IWizardPage getNextPage() {
		if(getWizard().performPage2Entry())
			return super.getNextPage();
		else
			return this;
	}
	
	@Override
	public boolean canFlipToNextPage() {
		// Use the original getNextPage
        return isPageComplete() && super.getNextPage() != null;
	}
	
	@Override
	public void setVisible(boolean visible) {
		if (visible) {
			// Clean up the project if we came from page 2
			if(getWizard().deeProject != null)
				getWizard().performPage2GoBack();
		} 
		super.setVisible(visible);
	}
	
}
