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
package descent.internal.ui.wizards.buildpaths;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;

import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import org.eclipse.ui.views.navigator.ResourceSorter;

import descent.internal.corext.util.Messages;

import descent.internal.ui.IJavaHelpContextIds;
import descent.internal.ui.dialogs.StatusInfo;
import descent.internal.ui.util.SWTUtil;
import descent.internal.ui.wizards.NewWizardMessages;
import descent.internal.ui.wizards.TypedElementSelectionValidator;
import descent.internal.ui.wizards.TypedViewerFilter;
import descent.internal.ui.wizards.dialogfields.DialogField;
import descent.internal.ui.wizards.dialogfields.IDialogFieldListener;
import descent.internal.ui.wizards.dialogfields.IStringButtonAdapter;
import descent.internal.ui.wizards.dialogfields.SelectionButtonDialogField;
import descent.internal.ui.wizards.dialogfields.StringButtonDialogField;

public class OutputLocationDialog extends StatusDialog {
	
	private StringButtonDialogField fContainerDialogField;
	private SelectionButtonDialogField fUseDefault;
	private SelectionButtonDialogField fUseSpecific;
	private StatusInfo fContainerFieldStatus;
	
	private IProject fCurrProject;
	private IPath fOutputLocation;
    private List fClassPathList;
		
	public OutputLocationDialog(Shell parent, CPListElement entryToEdit, List classPathList) {
		super(parent);
		setTitle(NewWizardMessages.OutputLocationDialog_title); 
		fContainerFieldStatus= new StatusInfo();
	
		OutputLocationAdapter adapter= new OutputLocationAdapter();

		fUseDefault= new SelectionButtonDialogField(SWT.RADIO);
		fUseDefault.setLabelText(NewWizardMessages.OutputLocationDialog_usedefault_label); 
		fUseDefault.setDialogFieldListener(adapter);		

		String label= Messages.format(NewWizardMessages.OutputLocationDialog_usespecific_label, entryToEdit.getPath().segment(0)); 
		fUseSpecific= new SelectionButtonDialogField(SWT.RADIO);
		fUseSpecific.setLabelText(label);
		fUseSpecific.setDialogFieldListener(adapter);		
		
		fContainerDialogField= new StringButtonDialogField(adapter);
		fContainerDialogField.setButtonLabel(NewWizardMessages.OutputLocationDialog_location_button); 
		fContainerDialogField.setDialogFieldListener(adapter);
		
		fUseSpecific.attachDialogField(fContainerDialogField);
		
		fCurrProject= entryToEdit.getJavaProject().getProject();
        fClassPathList= classPathList;
		classPathList.remove(entryToEdit);
		
		IPath outputLocation= (IPath) entryToEdit.getAttribute(CPListElement.OUTPUT);
		if (outputLocation == null) {
			fUseDefault.setSelection(true);
		} else {
			fUseSpecific.setSelection(true);
			fContainerDialogField.setText(outputLocation.removeFirstSegments(1).makeRelative().toString());
		}
	}
	
	
	protected Control createDialogArea(Composite parent) {
		Composite composite= (Composite)super.createDialogArea(parent);
		
		int widthHint= convertWidthInCharsToPixels(60);
		int indent= convertWidthInCharsToPixels(4);
		
		Composite inner= new Composite(composite, SWT.NONE);
		GridLayout layout= new GridLayout();
		layout.marginHeight= 0;
		layout.marginWidth= 0;
		layout.numColumns= 2;
		inner.setLayout(layout);
		
		fUseDefault.doFillIntoGrid(inner, 2);
		fUseSpecific.doFillIntoGrid(inner, 2);
		
		Text textControl= fContainerDialogField.getTextControl(inner);
		GridData textData= new GridData();
		textData.widthHint= widthHint;
		textData.grabExcessHorizontalSpace= true;
		textData.horizontalIndent= indent;
		textControl.setLayoutData(textData);
		
		Button buttonControl= fContainerDialogField.getChangeControl(inner);
		GridData buttonData= new GridData();
		buttonData.widthHint= SWTUtil.getButtonWidthHint(buttonControl);
		buttonControl.setLayoutData(buttonData);
		
		applyDialogFont(composite);		
		return composite;
	}

		
	// -------- OutputLocationAdapter --------

	private class OutputLocationAdapter implements IDialogFieldListener, IStringButtonAdapter {
		
		// -------- IDialogFieldListener
		
		public void dialogFieldChanged(DialogField field) {
			doStatusLineUpdate();
		}

		public void changeControlPressed(DialogField field) {
			doChangeControlPressed();
		}
	}
	
	protected void doChangeControlPressed() {
		IContainer container= chooseOutputLocation();
		if (container != null) {
			fContainerDialogField.setText(container.getProjectRelativePath().toString());
		}
	}
	
	
	protected void doStatusLineUpdate() {
		checkIfPathValid();
		warnIfPathDangerous();
		updateStatus(fContainerFieldStatus);
	}

	protected void checkIfPathValid() {
		fOutputLocation= null;
		fContainerFieldStatus.setOK();

		if (fUseDefault.isSelected()) {
			return;
		}
				
		String pathStr= fContainerDialogField.getText();
		if (pathStr.length() == 0) {
			fContainerFieldStatus.setError(""); //$NON-NLS-1$
			return;
		}
		IPath projectPath= fCurrProject.getFullPath();
				
		IPath path= projectPath.append(pathStr);
		
		IWorkspace workspace= fCurrProject.getWorkspace();		
		IStatus pathValidation= workspace.validatePath(path.toString(), IResource.PROJECT | IResource.FOLDER);
		if (!pathValidation.isOK()) {
			fContainerFieldStatus.setError(Messages.format(NewWizardMessages.OutputLocationDialog_error_invalidpath, pathValidation.getMessage())); 
			return;
		}
		
		IWorkspaceRoot root= workspace.getRoot();
		IResource res= root.findMember(path);
		if (res != null) {
			// if exists, must be a folder or project
			if (res.getType() == IResource.FILE) {
				fContainerFieldStatus.setError(NewWizardMessages.OutputLocationDialog_error_existingisfile); 
				return;
			}
            
            if (!checkIfFolderValid(path)) {
                fContainerFieldStatus.setError(Messages.format(NewWizardMessages.OutputLocationDialog_error_invalidFolder, path)); 
               return;
            }
		}
		fOutputLocation= path;
	}
	
	private void warnIfPathDangerous() {
		if (!fContainerFieldStatus.isOK())
			return;
		
		if (fUseDefault.isSelected())
			return;
		
		String pathStr= fContainerDialogField.getText();
		if (pathStr.length() == 0)
			return;
		
		Path outputPath= (new Path(pathStr));
		pathStr= outputPath.lastSegment();
		if (pathStr.equals(".settings") && outputPath.segmentCount() == 1) { //$NON-NLS-1$
			fContainerFieldStatus.setWarning(NewWizardMessages.OutputLocation_SettingsAsLocation);
			return;
		}
		
		if (pathStr.charAt(0) == '.' && pathStr.length() > 1) {
			fContainerFieldStatus.setWarning(Messages.format(NewWizardMessages.OutputLocation_DotAsLocation, pathStr));
			return;
		}
	}
    
    /**
     * Iterate over the list of class path entries and check 
     * wheter the new path points to a location where a 
     * source folder has already been established.
     * 
     * @param path the new path
     * @return <code>false</code> if the path belongs to 
     * a folder which is already taken as source folder, 
     * <code>true</code> otherwise
     */
    private boolean checkIfFolderValid(IPath path) {
        Iterator iterator= fClassPathList.iterator();
        while (iterator.hasNext()) {
            CPListElement element= (CPListElement)iterator.next();
            if (element.getPath().equals(path))
                return false;
        }
        return true;
    }
	
		
	public IPath getOutputLocation() {
		return fOutputLocation;
	}
		
	/*
	 * @see org.eclipse.jface.window.Window#configureShell(Shell)
	 */
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(newShell, IJavaHelpContextIds.OUTPUT_LOCATION_DIALOG);
	}
	
	// ---------- util method ------------

	private IContainer chooseOutputLocation() {
		IWorkspaceRoot root= fCurrProject.getWorkspace().getRoot();
		final Class[] acceptedClasses= new Class[] { IProject.class, IFolder.class };
		IProject[] allProjects= root.getProjects();
		ArrayList rejectedElements= new ArrayList(allProjects.length);
		for (int i= 0; i < allProjects.length; i++) {
			if (!allProjects[i].equals(fCurrProject)) {
				rejectedElements.add(allProjects[i]);
			}
		}
		ViewerFilter filter= new TypedViewerFilter(acceptedClasses, rejectedElements.toArray());

		ILabelProvider lp= new WorkbenchLabelProvider();
		ITreeContentProvider cp= new WorkbenchContentProvider();

		IResource initSelection= null;
		if (fOutputLocation != null) {
			initSelection= root.findMember(fOutputLocation);
		}

		FolderSelectionDialog dialog= new FolderSelectionDialog(getShell(), lp, cp);
		dialog.setTitle(NewWizardMessages.OutputLocationDialog_ChooseOutputFolder_title); 
        
        dialog.setValidator(new ISelectionStatusValidator() {
            ISelectionStatusValidator validator= new TypedElementSelectionValidator(acceptedClasses, false);
            public IStatus validate(Object[] selection) {
                IStatus typedStatus= validator.validate(selection);
                if (!typedStatus.isOK())
                    return typedStatus;           
                if (selection[0] instanceof IFolder) {
                    IFolder folder= (IFolder) selection[0];
                    boolean valid= checkIfFolderValid(folder.getFullPath());
                    if (!valid) {
                        return new StatusInfo(IStatus.ERROR, Messages.format(NewWizardMessages.OutputLocationDialog_error_invalidFolder, folder.getFullPath())); 
                    }
                }
                return new StatusInfo();
            }
        });
		dialog.setMessage(NewWizardMessages.OutputLocationDialog_ChooseOutputFolder_description); 
		dialog.addFilter(filter);
		dialog.setInput(root);
		dialog.setInitialSelection(initSelection);
		dialog.setSorter(new ResourceSorter(ResourceSorter.NAME));

		if (dialog.open() == Window.OK) {
			return (IContainer)dialog.getFirstResult();
		}
		return null;
	}
	


}
