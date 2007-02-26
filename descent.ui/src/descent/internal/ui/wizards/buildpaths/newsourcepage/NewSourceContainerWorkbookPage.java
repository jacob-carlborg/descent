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

package descent.internal.ui.wizards.buildpaths.newsourcepage;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import org.eclipse.core.resources.IFolder;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;

import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;

import descent.core.IClasspathEntry;
import descent.core.IJavaProject;
import descent.core.IPackageFragmentRoot;
import descent.core.JavaCore;
import descent.core.JavaModelException;

import descent.internal.corext.buildpath.ClasspathModifier;
import descent.internal.corext.buildpath.ResetAllOutputFoldersOperation;
import descent.internal.corext.buildpath.ClasspathModifier.IClasspathModifierListener;
import descent.internal.corext.util.Messages;

import descent.internal.ui.JavaPlugin;
import descent.internal.ui.preferences.ScrolledPageContent;
import descent.internal.ui.util.ExceptionHandler;
import descent.internal.ui.util.PixelConverter;
import descent.internal.ui.util.ViewerPane;
import descent.internal.ui.wizards.NewWizardMessages;
import descent.internal.ui.wizards.buildpaths.BuildPathBasePage;
import descent.internal.ui.wizards.buildpaths.BuildPathsBlock;
import descent.internal.ui.wizards.buildpaths.CPListElement;
import descent.internal.ui.wizards.buildpaths.CPListElementAttribute;
import descent.internal.ui.wizards.buildpaths.newsourcepage.DialogPackageExplorerActionGroup.DialogExplorerActionContext;
import descent.internal.ui.wizards.dialogfields.DialogField;
import descent.internal.ui.wizards.dialogfields.IDialogFieldListener;
import descent.internal.ui.wizards.dialogfields.LayoutUtil;
import descent.internal.ui.wizards.dialogfields.ListDialogField;
import descent.internal.ui.wizards.dialogfields.SelectionButtonDialogField;
import descent.internal.ui.wizards.dialogfields.StringDialogField;

public class NewSourceContainerWorkbookPage extends BuildPathBasePage implements IClasspathModifierListener {
    
    public static final String OPEN_SETTING= "descent.internal.ui.wizards.buildpaths.NewSourceContainerPage.openSetting";  //$NON-NLS-1$
    
    private ListDialogField fClassPathList;
    private HintTextGroup fHintTextGroup;
    private DialogPackageExplorer fPackageExplorer;
    private SelectionButtonDialogField fUseFolderOutputs;
	private final StringDialogField fOutputLocationField;
	private final BuildPathsBlock fBuildPathsBlock;
	
	private IJavaProject fJavaProject;


    /**
     * Constructor of the <code>NewSourceContainerWorkbookPage</code> which consists of 
     * a tree representing the project, a toolbar with the available actions, an area 
     * containing hyperlinks that perform the same actions as those in the toolbar but 
     * additionally with some short description.
     * 
     * @param classPathList
     * @param outputLocationField
     * @param context a runnable context, can be <code>null</code>
     */
    public NewSourceContainerWorkbookPage(ListDialogField classPathList, StringDialogField outputLocationField, IRunnableContext context, BuildPathsBlock buildPathsBlock) {
        fClassPathList= classPathList;
		fOutputLocationField= outputLocationField;
		fBuildPathsBlock= buildPathsBlock;
    
        fUseFolderOutputs= new SelectionButtonDialogField(SWT.CHECK);
        fUseFolderOutputs.setSelection(false);
        fUseFolderOutputs.setLabelText(NewWizardMessages.SourceContainerWorkbookPage_folders_check); 
        
		fPackageExplorer= new DialogPackageExplorer();
		fHintTextGroup= new HintTextGroup(fPackageExplorer, outputLocationField, fUseFolderOutputs, context, this);

     }
    
    /**
     * Initialize the controls displaying
     * the content of the java project and saving 
     * the '.classpath' and '.project' file.
     * 
     * Must be called before initializing the 
     * controls using <code>getControl(Composite)</code>.
     * 
     * @param javaProject the current java project
     */
    public void init(IJavaProject javaProject) {
		fJavaProject= javaProject;
        fHintTextGroup.setJavaProject(javaProject);
        
        fPackageExplorer.setInput(javaProject);
		
		boolean useFolderOutputs= false;
		List cpelements= fClassPathList.getElements();
		for (int i= 0; i < cpelements.size() && !useFolderOutputs; i++) {
			CPListElement cpe= (CPListElement) cpelements.get(i);
			if (cpe.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
				if (cpe.getAttribute(CPListElement.OUTPUT) != null) {
					useFolderOutputs= true;
				}
			}
		}
		fUseFolderOutputs.setSelection(useFolderOutputs);
    }
    
     
    /**
     * Initializes controls and return composite containing
     * these controls.
     * 
     * Before calling this method, make sure to have 
     * initialized this instance with a java project 
     * using <code>init(IJavaProject)</code>.
     * 
     * @param parent the parent composite
     * @return composite containing controls
     * 
     * @see #init(IJavaProject)
     */
    public Control getControl(Composite parent) {
        final int[] sashWeight= {60};
        final IPreferenceStore preferenceStore= JavaPlugin.getDefault().getPreferenceStore();
        preferenceStore.setDefault(OPEN_SETTING, true);
        
        // ScrolledPageContent is needed for resizing on expand the expandable composite
        ScrolledPageContent scrolledContent = new ScrolledPageContent(parent);
        Composite body= scrolledContent.getBody();
        body.setLayout(new GridLayout());
        
        final SashForm sashForm= new SashForm(body, SWT.VERTICAL | SWT.NONE);
        sashForm.setFont(sashForm.getFont());
        
        ViewerPane pane= new ViewerPane(sashForm, SWT.BORDER | SWT.FLAT);
        pane.setContent(fPackageExplorer.createControl(pane));
		fPackageExplorer.setContentProvider();
        
        final ExpandableComposite excomposite= new ExpandableComposite(sashForm, SWT.NONE, ExpandableComposite.TWISTIE | ExpandableComposite.CLIENT_INDENT);
        excomposite.setFont(sashForm.getFont());
        excomposite.setText(NewWizardMessages.NewSourceContainerWorkbookPage_HintTextGroup_title);
        final boolean isExpanded= preferenceStore.getBoolean(OPEN_SETTING);
        excomposite.setExpanded(isExpanded);
        excomposite.addExpansionListener(new ExpansionAdapter() {
                       public void expansionStateChanged(ExpansionEvent e) {
                           ScrolledPageContent parentScrolledComposite= getParentScrolledComposite(excomposite);
                           if (parentScrolledComposite != null) {
                              boolean expanded= excomposite.isExpanded();
                              parentScrolledComposite.reflow(true);
                              adjustSashForm(sashWeight, sashForm, expanded);
                              preferenceStore.setValue(OPEN_SETTING, expanded);
                           }
                       }
                 });
        
        excomposite.setClient(fHintTextGroup.createControl(excomposite));
        fUseFolderOutputs.doFillIntoGrid(body, 1);
		
	    final DialogPackageExplorerActionGroup actionGroup= new DialogPackageExplorerActionGroup(fHintTextGroup, this);
		   
		
        fUseFolderOutputs.setDialogFieldListener(new IDialogFieldListener() {
            public void dialogFieldChanged(DialogField field) {
                boolean isUseFolders= fUseFolderOutputs.isSelected();
                if (isUseFolders) {
                    ResetAllOutputFoldersOperation op= new ResetAllOutputFoldersOperation(NewSourceContainerWorkbookPage.this, fHintTextGroup);
                    try {
                        op.run(null);
                    } catch (InvocationTargetException e) {
                        ExceptionHandler.handle(e, getShell(),
                                Messages.format(NewWizardMessages.NewSourceContainerWorkbookPage_Exception_Title, op.getName()), e.getMessage()); 
                    }
                }
				fPackageExplorer.showOutputFolders(isUseFolders);
                try {
                    ISelection selection= fPackageExplorer.getSelection();
                    actionGroup.refresh(new DialogExplorerActionContext(selection, fJavaProject));
                } catch (JavaModelException e) {
                    ExceptionHandler.handle(e, getShell(),
                            NewWizardMessages.NewSourceContainerWorkbookPage_Exception_refresh, e.getMessage()); 
                }
            }
        });
        
        Composite outputLocation= new Composite(body, SWT.NONE);
        outputLocation.setLayout(new GridLayout(2, false));
        outputLocation.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        
		LayoutUtil.doDefaultLayout(outputLocation, new DialogField[] {fOutputLocationField }, true, SWT.DEFAULT, SWT.DEFAULT);
		LayoutUtil.setHorizontalGrabbing(fOutputLocationField.getTextControl(null));
        
        // Create toolbar with actions on the left
        ToolBarManager tbm= actionGroup.createLeftToolBarManager(pane);
        pane.setTopCenter(null);
        pane.setTopLeft(tbm.getControl());
        
        // Create toolbar with help on the right
        tbm= actionGroup.createLeftToolBar(pane);
        pane.setTopRight(tbm.getControl());
        
        fHintTextGroup.setActionGroup(actionGroup);
        fPackageExplorer.setActionGroup(actionGroup);
        actionGroup.addListener(fHintTextGroup);
        
		sashForm.setWeights(new int[] {60, 40});
		adjustSashForm(sashWeight, sashForm, excomposite.isExpanded());
		GridData gd= new GridData(GridData.FILL_BOTH);
		PixelConverter converter= new PixelConverter(parent);
		gd.heightHint= converter.convertHeightInCharsToPixels(20);
		sashForm.setLayoutData(gd);
        
        fUseFolderOutputs.dialogFieldChanged();
        
        parent.layout(true);

        return scrolledContent;
    }
    
    /**
     * Adjust the size of the sash form.
     * 
     * @param sashWeight the weight to be read or written
     * @param sashForm the sash form to apply the new weights to
     * @param isExpanded <code>true</code> if the expandable composite is 
     * expanded, <code>false</code> otherwise
     */
    private void adjustSashForm(int[] sashWeight, SashForm sashForm, boolean isExpanded) {
        if (isExpanded) {
            int upperWeight= sashWeight[0];
            sashForm.setWeights(new int[]{upperWeight, 100 - upperWeight});
        }
        else {
            // TODO Dividing by 10 because of https://bugs.eclipse.org/bugs/show_bug.cgi?id=81939
            sashWeight[0]= sashForm.getWeights()[0] / 10;
            sashForm.setWeights(new int[]{95, 5});
        }
        sashForm.layout(true);
    }
    
    /**
     * Get the scrolled page content of the given control by 
     * traversing the parents.
     * 
     * @param control the control to get the scrolled page content for 
     * @return the scrolled page content or <code>null</code> if none found
     */
    private ScrolledPageContent getParentScrolledComposite(Control control) {
       Control parent= control.getParent();
       while (!(parent instanceof ScrolledPageContent)) {
           parent= parent.getParent();
       }
       if (parent instanceof ScrolledPageContent) {
           return (ScrolledPageContent) parent;
       }
       return null;
   }
    
    /**
     * Get the active shell.
     * 
     * @return the active shell
     */
    private Shell getShell() {
        return JavaPlugin.getActiveWorkbenchShell();
    }

    /* (non-Javadoc)
     * @see descent.internal.ui.wizards.buildpaths.BuildPathBasePage#getSelection()
     */
    public List getSelection() {
        List selectedList= new ArrayList();
        
        IJavaProject project= fHintTextGroup.getJavaProject();
        try {
            List list= fHintTextGroup.getSelection().toList();
            List existingEntries= ClasspathModifier.getExistingEntries(project);
        
            for(int i= 0; i < list.size(); i++) {
                Object obj= list.get(i);
                if (obj instanceof IPackageFragmentRoot) {
                    IPackageFragmentRoot element= (IPackageFragmentRoot)obj;
                    CPListElement cpElement= ClasspathModifier.getClasspathEntry(existingEntries, element); 
                    selectedList.add(cpElement);
                }
                else if (obj instanceof IJavaProject) {
                    IClasspathEntry entry= ClasspathModifier.getClasspathEntryFor(project.getPath(), project, IClasspathEntry.CPE_SOURCE);
                    if (entry == null)
                        continue;
                    CPListElement cpElement= CPListElement.createFromExisting(entry, project);
                    selectedList.add(cpElement);
                }
            }
        } catch (JavaModelException e) {
            return new ArrayList();
        }
        return selectedList;
    }

    /* (non-Javadoc)
     * @see descent.internal.ui.wizards.buildpaths.BuildPathBasePage#setSelection(java.util.List)
     */
    public void setSelection(List selection, boolean expand) {
		// page switch
		
        if (selection.size() == 0)
            return;
	    
		List cpEntries= new ArrayList();
		
		for (int i= 0; i < selection.size(); i++) {
			Object obj= selection.get(i);
			if (obj instanceof CPListElement) {
				CPListElement element= (CPListElement) obj;
				if (element.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
					cpEntries.add(element);
				}
			} else if (obj instanceof CPListElementAttribute) {
				CPListElementAttribute attribute= (CPListElementAttribute)obj;
				CPListElement element= attribute.getParent();
				if (element.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
					cpEntries.add(element);
				}
			}
		}
		
        // refresh classpath
        List list= fClassPathList.getElements();
        IClasspathEntry[] entries= new IClasspathEntry[list.size()];
        for(int i= 0; i < list.size(); i++) {
            CPListElement entry= (CPListElement) list.get(i);
            entries[i]= entry.getClasspathEntry(); 
        }
        try {
			fJavaProject.setRawClasspath(entries, null);
            fPackageExplorer.refresh();
        } catch (JavaModelException e) {
            JavaPlugin.log(e);
        }
        
        fPackageExplorer.setSelection(cpEntries);
    }

    /* (non-Javadoc)
     * @see descent.internal.ui.wizards.buildpaths.BuildPathBasePage#isEntryKind(int)
     */
    public boolean isEntryKind(int kind) {
        return kind == IClasspathEntry.CPE_SOURCE;
    }
    
    /**
     * Update <code>fClassPathList</code>.
     */
    public void classpathEntryChanged(List newEntries) {
        fClassPathList.setElements(newEntries);
    }
    

	public void commitDefaultOutputFolder() {
		if (!fBuildPathsBlock.isOKStatus())
			return;
		try {
			IPath path= new Path(fOutputLocationField.getText()).makeAbsolute();
			IPath outputLocation= fJavaProject.getOutputLocation();
			if (path.equals(outputLocation))
				return;
			
			if (!outputLocation.equals(fJavaProject.getPath())) {
				IFolder folder= fJavaProject.getProject().getWorkspace().getRoot().getFolder(outputLocation);
				if (folder.exists() && JavaCore.create(folder) == null) {
					folder.delete(true, null);
				}
			}
	        fJavaProject.setOutputLocation(path, null);
        } catch (JavaModelException e) {
	     	JavaPlugin.log(e);
        } catch (CoreException e) {
	        JavaPlugin.log(e);
        }
    }
}
