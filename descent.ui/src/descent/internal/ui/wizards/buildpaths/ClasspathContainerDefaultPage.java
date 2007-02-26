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
package descent.internal.ui.wizards.buildpaths;

import java.util.ArrayList;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import descent.core.IClasspathEntry;
import descent.core.IJavaProject;
import descent.core.JavaCore;
import descent.internal.ui.IJavaHelpContextIds;
import descent.internal.ui.JavaPluginImages;
import descent.internal.ui.dialogs.StatusInfo;
import descent.internal.ui.wizards.NewWizardMessages;
import descent.internal.ui.wizards.dialogfields.DialogField;
import descent.internal.ui.wizards.dialogfields.IDialogFieldListener;
import descent.internal.ui.wizards.dialogfields.LayoutUtil;
import descent.internal.ui.wizards.dialogfields.StringDialogField;
import descent.ui.wizards.IClasspathContainerPage;
import descent.ui.wizards.IClasspathContainerPageExtension;
import descent.ui.wizards.NewElementWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import org.eclipse.jface.dialogs.Dialog;

import org.eclipse.ui.PlatformUI;

/**
  */
public class ClasspathContainerDefaultPage extends NewElementWizardPage implements IClasspathContainerPage, IClasspathContainerPageExtension {

	private StringDialogField fEntryField;
	private ArrayList fUsedPaths;

	/**
	 * Constructor for ClasspathContainerDefaultPage.
	 */
	public ClasspathContainerDefaultPage() {
		super("ClasspathContainerDefaultPage"); //$NON-NLS-1$
		setTitle(NewWizardMessages.ClasspathContainerDefaultPage_title); 
		setDescription(NewWizardMessages.ClasspathContainerDefaultPage_description); 
		setImageDescriptor(JavaPluginImages.DESC_WIZBAN_ADD_LIBRARY);
		
		fUsedPaths= new ArrayList();
		
		fEntryField= new StringDialogField();
		fEntryField.setLabelText(NewWizardMessages.ClasspathContainerDefaultPage_path_label); 
		fEntryField.setDialogFieldListener(new IDialogFieldListener() {
			public void dialogFieldChanged(DialogField field) {
				validatePath();
			}
		});
		validatePath();
	}

	private void validatePath() {
		StatusInfo status= new StatusInfo();
		String str= fEntryField.getText();
		if (str.length() == 0) {
			status.setError(NewWizardMessages.ClasspathContainerDefaultPage_path_error_enterpath); 
		} else if (!Path.ROOT.isValidPath(str)) {
			status.setError(NewWizardMessages.ClasspathContainerDefaultPage_path_error_invalidpath); 
		} else {
			IPath path= new Path(str);
			if (path.segmentCount() == 0) {
				status.setError(NewWizardMessages.ClasspathContainerDefaultPage_path_error_needssegment); 
			} else if (fUsedPaths.contains(path)) {
				status.setError(NewWizardMessages.ClasspathContainerDefaultPage_path_error_alreadyexists); 
			}
		}
		updateStatus(status);
	}

	/* (non-Javadoc)
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		Composite composite= new Composite(parent, SWT.NONE);
		GridLayout layout= new GridLayout();
		layout.numColumns= 1;
		composite.setLayout(layout);
		
		fEntryField.doFillIntoGrid(composite, 2);
		LayoutUtil.setHorizontalGrabbing(fEntryField.getTextControl(null));
		
		fEntryField.setFocus();
		
		setControl(composite);
		Dialog.applyDialogFont(composite);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, IJavaHelpContextIds.CLASSPATH_CONTAINER_DEFAULT_PAGE);
	}

	/* (non-Javadoc)
	 * @see IClasspathContainerPage#finish()
	 */
	public boolean finish() {
		return true;
	}
	
	/* (non-Javadoc)
	 * @see IClasspathContainerPage#getSelection()
	 */
	public IClasspathEntry getSelection() {
		return JavaCore.newContainerEntry(new Path(fEntryField.getText()));
	}
	
	/* (non-Javadoc)
	 * @see descent.ui.wizards.IClasspathContainerPageExtension#initialize(descent.core.IJavaProject, descent.core.IClasspathEntry)
	 */
	public void initialize(IJavaProject project, IClasspathEntry[] currentEntries) {
		for (int i= 0; i < currentEntries.length; i++) {
			IClasspathEntry curr= currentEntries[i];
			if (curr.getEntryKind() == IClasspathEntry.CPE_CONTAINER) {
				fUsedPaths.add(curr.getPath());
			}
		}
	}		

	/* (non-Javadoc)
	 * @see IClasspathContainerPage#setSelection(IClasspathEntry)
	 */
	public void setSelection(IClasspathEntry containerEntry) {
		if (containerEntry != null) {
			fUsedPaths.remove(containerEntry.getPath());
			fEntryField.setText(containerEntry.getPath().toString());
		} else {
			fEntryField.setText(""); //$NON-NLS-1$
		}
	}



}
