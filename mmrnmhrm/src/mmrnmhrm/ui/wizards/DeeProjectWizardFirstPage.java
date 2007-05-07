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

import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.model.DeeModelManager;
import mmrnmhrm.core.model.DeeModelRoot;
import mmrnmhrm.core.model.DeeProject;
import mmrnmhrm.util.ui.LayoutUtil;
import mmrnmhrm.util.ui.SWTUtil2;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
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
	
	@Override
	public IWizardPage getNextPage() {
		return super.getNextPage();
		// TODO: execute createDeeProject
	}
	
	public void createDeeProject(final IProgressMonitor monitor) throws CoreException {
		IWorkspaceRoot workspaceRoot = DeeCore.getWorkspaceRoot();
		IProject project = workspaceRoot.getProject(getProjectName());
		project.create(monitor);
		project.open(monitor);

		getWizard().deeProject = DeeModelManager.createDeeProject(project);
	}

	
	public void deleteDeeProject(final IProgressMonitor monitor) throws CoreException {
		DeeProject deeProject = getWizard().deeProject;
		deeProject.getProject().delete(false, monitor);

		DeeModelManager.getRoot().removeDeeProject(deeProject);
	}
}
