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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.viewers.IStructuredSelection;

import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import descent.core.IJavaElement;
import descent.core.IJavaProject;
import descent.core.JavaModelException;

import descent.internal.corext.buildpath.ClasspathModifier;
import descent.internal.corext.buildpath.IClasspathInformationProvider;
import descent.internal.corext.buildpath.IPackageExplorerActionListener;
import descent.internal.corext.buildpath.PackageExplorerActionEvent;
import descent.internal.corext.util.Messages;

import descent.internal.ui.JavaPlugin;
import descent.internal.ui.preferences.ScrolledPageContent;
import descent.internal.ui.util.ExceptionHandler;
import descent.internal.ui.util.PixelConverter;
import descent.internal.ui.wizards.NewWizardMessages;
import descent.internal.ui.wizards.buildpaths.CPListElementAttribute;
import descent.internal.ui.wizards.buildpaths.newsourcepage.ClasspathModifierQueries.IAddArchivesQuery;
import descent.internal.ui.wizards.buildpaths.newsourcepage.ClasspathModifierQueries.IAddLibrariesQuery;
import descent.internal.ui.wizards.buildpaths.newsourcepage.ClasspathModifierQueries.ICreateFolderQuery;
import descent.internal.ui.wizards.buildpaths.newsourcepage.ClasspathModifierQueries.IInclusionExclusionQuery;
import descent.internal.ui.wizards.buildpaths.newsourcepage.ClasspathModifierQueries.ILinkToQuery;
import descent.internal.ui.wizards.buildpaths.newsourcepage.ClasspathModifierQueries.IOutputLocationQuery;
import descent.internal.ui.wizards.buildpaths.newsourcepage.ClasspathModifierQueries.IRemoveLinkedFolderQuery;
import descent.internal.ui.wizards.buildpaths.newsourcepage.ClasspathModifierQueries.OutputFolderQuery;
import descent.internal.ui.wizards.buildpaths.newsourcepage.DialogPackageExplorerActionGroup.DialogExplorerActionContext;
import descent.internal.ui.wizards.dialogfields.SelectionButtonDialogField;
import descent.internal.ui.wizards.dialogfields.StringDialogField;

/**
 * Displays a set of available links to modify or adjust the project.
 * The links contain a short description about the consequences of 
 * this action.
 * 
 * The content depends on the selection made on the project.
 * If selection changes, then the <code>HintTextGroup</code> will be 
 * notified through the <code>IPackageExplorerActionListener</code> interface. 
 */
public final class HintTextGroup implements IClasspathInformationProvider, IPackageExplorerActionListener {
	
	/**
	 * Order of actions in the group. First is first.
	 * Only actions in ACTION_ORDER are part of the group.
	 */
	private final static int[] ACTION_ORDER= {
		IClasspathInformationProvider.CREATE_FOLDER,
		IClasspathInformationProvider.CREATE_LINK,
		IClasspathInformationProvider.EDIT_FILTERS,
		IClasspathInformationProvider.EXCLUDE,
		IClasspathInformationProvider.INCLUDE,
		IClasspathInformationProvider.UNEXCLUDE,
		IClasspathInformationProvider.UNINCLUDE,
		IClasspathInformationProvider.EDIT_OUTPUT,
		IClasspathInformationProvider.CREATE_OUTPUT,
		IClasspathInformationProvider.ADD_SEL_SF_TO_BP,
		IClasspathInformationProvider.REMOVE_FROM_BP,
		IClasspathInformationProvider.ADD_SEL_LIB_TO_BP,
		IClasspathInformationProvider.ADD_LIB_TO_BP,
		IClasspathInformationProvider.ADD_JAR_TO_BP
	};
    
    private StringDialogField fOutputLocationField;
    private Composite fTopComposite;
    private DialogPackageExplorerActionGroup fActionGroup;
    private DialogPackageExplorer fPackageExplorer;
    private IRunnableContext fRunnableContext;
    private IJavaProject fCurrJProject;
    private List fNewFolders;
    private String fOldOutputLocation;
    private HashMap fImageMap;
	private final NewSourceContainerWorkbookPage fPage;
    
    public HintTextGroup(DialogPackageExplorer packageExplorer, StringDialogField outputLocationField, SelectionButtonDialogField useFolderOutputs, IRunnableContext runnableContext, NewSourceContainerWorkbookPage page) {
        fPackageExplorer= packageExplorer;
        fRunnableContext= runnableContext;
		fPage = page;
        fCurrJProject= null;
        fNewFolders= new ArrayList();
        fImageMap= new HashMap();
        fOutputLocationField= outputLocationField;
    }
    
    public Composite createControl(Composite parent) {
        fTopComposite= new Composite(parent, SWT.NONE);
        fTopComposite.setFont(parent.getFont());
        
        GridData gridData= new GridData(GridData.FILL_BOTH);
        PixelConverter converter= new PixelConverter(parent);
        gridData.heightHint= converter.convertHeightInCharsToPixels(12);
        GridLayout gridLayout= new GridLayout();
        gridLayout.marginWidth= 0;//-converter.convertWidthInCharsToPixels(2);
        gridLayout.marginHeight= 0;//= -4;
        fTopComposite.setLayout(gridLayout);
        fTopComposite.setLayoutData(gridData);
        fTopComposite.setData(null);
        fTopComposite.addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent e) {
                Collection collection= fImageMap.values();
                Iterator iterator= collection.iterator();
                while(iterator.hasNext()) {
                    Image image= (Image)iterator.next();
                    image.dispose();
                }
            }
        });
        return fTopComposite;
    }
    
    private Shell getShell() {
        return JavaPlugin.getActiveWorkbenchShell();
    }
    
    /**
     * Set the java project
     * 
     * @param jProject the java project to work on
     */
    public void setJavaProject(IJavaProject jProject) {
        fCurrJProject= jProject;
        fOldOutputLocation= fOutputLocationField.getText();
    }
    
    /**
     * An action group managing the actions needed by 
     * the <code>HintTextGroup</code>.
     * 
     * Note: This method has to be called on initialization.
     * Calling this method in the constructor is not 
     * possible because the actions might need a reference to 
     * this class.
     * 
     * @param actionGroup the action group containing the necessary 
     * actions
     * 
     * @see DialogPackageExplorerActionGroup
     */
    public void setActionGroup(DialogPackageExplorerActionGroup actionGroup) {
        fActionGroup= actionGroup;
    }
    
    /**
     * Creates a form text.
     * 
     * @param parent the parent to put the form text on
     * @param text the form text to be displayed
     * @return the created form text
     * 
     * @see FormToolkit#createFormText(org.eclipse.swt.widgets.Composite, boolean)
     */
    private FormText createFormText(Composite parent, String text) {
        FormToolkit toolkit= new FormToolkit(getShell().getDisplay());
        try {
        	FormText formText= toolkit.createFormText(parent, true);
        	formText.setFont(parent.getFont());
			try {
			    formText.setText(text, true, false);
			} catch (IllegalArgumentException e) {
			    formText.setText(e.getMessage(), false, false);
			    JavaPlugin.log(e);
			}
			formText.marginHeight= 2;
			formText.marginWidth= 0;
			formText.setBackground(null);
			formText.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
			return formText;
		} finally {
	        toolkit.dispose();
		}
    }
    
    /**
     * Create a label with a hyperlink and a picture.
     * 
     * @param parent the parent widget of the label
     * @param text the text of the label
     * @param action the action to be executed if the hyperlink is activated
     * @param context the runnable context under which the action is executed
     */
    private void createLabel(Composite parent, String text, final ClasspathModifierAction action, final IRunnableContext context) {
        FormText formText= createFormText(parent, text);
        Image image= (Image)fImageMap.get(action.getId());
        if (image == null) {
            image= action.getImageDescriptor().createImage();
            fImageMap.put(action.getId(), image);
        }
        formText.setImage("defaultImage", image); //$NON-NLS-1$
        formText.addHyperlinkListener(new HyperlinkAdapter() {

            public void linkActivated(HyperlinkEvent e) {
                try {
                	fPage.commitDefaultOutputFolder();
                    context.run(false, false, action.getOperation());
                } catch (InvocationTargetException err) {
                    ExceptionHandler.handle(err, getShell(), Messages.format(NewWizardMessages.HintTextGroup_Exception_Title, action.getName()), err.getMessage()); 
                } catch (InterruptedException err) {
                    // Cancel pressed
                }
            }
            
        });
    }
    
    /**
     * Returns the current selection which is a list of 
     * elements in a tree.
     * 
     * @return the current selection
     * @see IClasspathInformationProvider#getSelection()
     */
    public IStructuredSelection getSelection() {
        return fPackageExplorer.getSelection();
    }
    
    /**
     * Set the selection for the <code>DialogPackageExplorer</code>
     * 
     * @param elements a list of elements to be selected
     * 
     * @see DialogPackageExplorer#setSelection(List)
     */
    public void setSelection(List elements) {
        fPackageExplorer.setSelection(elements);
    }
    
    /**
     * Returns the Java project.
     * 
     * @see IClasspathInformationProvider#getJavaProject()
     */
    public IJavaProject getJavaProject() {
        return fCurrJProject;
    }
    
    /**
     * Handle the result of an action. Note that first, the exception has to be 
     * checked and computation can only continue if the exception is <code>null</code>.
     * 
     * @see IClasspathInformationProvider#handleResult(List, CoreException, int)
     */
    public void handleResult(List resultElements, CoreException exception, int actionType) {
        if (exception != null) {
            ExceptionHandler.handle(exception, getShell(), Messages.format(NewWizardMessages.HintTextGroup_Exception_Title_refresh, fActionGroup.getAction(actionType).getName()), exception.getLocalizedMessage()); 
            return;
        }
        
        switch(actionType) {
            case CREATE_FOLDER: handleFolderCreation(resultElements); break;
            case CREATE_LINK: handleFolderCreation(resultElements); break;
            case EDIT_FILTERS: defaultHandle(resultElements, false); break;
            case ADD_SEL_SF_TO_BP: case ADD_SEL_LIB_TO_BP: case ADD_JAR_TO_BP: case ADD_LIB_TO_BP:	
            	handleAddToCP(resultElements); break;
            case REMOVE_FROM_BP: handleRemoveFromBP(resultElements, false); break;
            case INCLUDE: defaultHandle(resultElements, true); break;
            case EXCLUDE: defaultHandle(resultElements, false); break;
            case UNINCLUDE: defaultHandle(resultElements, false); break;
            case UNEXCLUDE: defaultHandle(resultElements, true); break;
            case RESET: defaultHandle(resultElements, false); break;
            case EDIT_OUTPUT: handleEditOutputFolder(resultElements); break;
            case RESET_ALL: handleResetAll(); break;
            default: break;
        }
    }
    
    /**
     * Default result handle. This includes:
     * <li>Set the selection of the <code>fPackageExplorer</code>
     * to the result object</li>
     * <li>Force the hint text to be rebuilt if necessary</li>
     *  
     * @param result the result list of object to be selected by the 
     * <code>fPackageExplorer</code>
     * @param forceRebuild <code>true</code> if the area containing 
     * the links and their descriptions should be rebuilt, <code>
     * false</code> otherwise
     * 
     * @see DialogPackageExplorer#setSelection(List)
     * @see DialogPackageExplorerActionGroup#refresh(DialogExplorerActionContext)
     */
    private void defaultHandle(List result, boolean forceRebuild) {
        try {
            fPackageExplorer.setSelection(result);
            if (forceRebuild) {
                fActionGroup.refresh(new DialogExplorerActionContext(result, fCurrJProject));
            }
        } catch (JavaModelException e) {
            ExceptionHandler.handle(e, getShell(), NewWizardMessages.HintTextGroup_Exception_Title_refresh, e.getLocalizedMessage()); 
        }
    }
    
    /**
     * Handle folder creation. This includes:
     * <li>Set the selection of the <code>fPackageExplorer</code>
     * to the result object, unless the result object is <code>
     * null</code></li>
     * <li>Add the created folder to the list of new folders</li>
     * <li>Adjust the text of <code>fOutputLocationField</code> 
     * and add the project's new output location to the list of 
     * new folders, if necessary
     * 
     * In this case, the list consists only of one element on which the 
     * new folder has been created
     *  
     * @param result a list with only one element to be selected by the 
     * <code>fPackageExplorer</code>, or an empty list if creation was 
     * aborted
     */
    private void handleFolderCreation(List result) {
        if (result.size() == 1) {
            fNewFolders.add(result.get(0));
            fPackageExplorer.setSelection(result);
            setOutputLocationFieldText(getOldOutputLocation());
        }
    }
    
    /**
     * Handle adding to classpath. This includes:
     * <li>Set the selection of the <code>fPackageExplorer</code>
     * to the list of result elements</li>
     * <li>Set adjust the text of <code>fOutputLocationField</code> 
     * and add the project's new output location to the list of 
     * new folders, if necessary
     *  
     * @param result the result list of object to be selected by the 
     * <code>fPackageExplorer</code>
     */
    private void handleAddToCP(List result) {
        try {
            if (containsJavaProject(result)) {
                fPackageExplorer.setSelection(result);
                fActionGroup.refresh(new DialogExplorerActionContext(result, fCurrJProject));
            }
            else
                fPackageExplorer.setSelection(result);
            setOutputLocationFieldText(getOldOutputLocation());
        } catch (JavaModelException e) {
            ExceptionHandler.handle(e, getShell(), NewWizardMessages.HintTextGroup_Exception_Title_refresh, e.getLocalizedMessage()); 
        }
    }
    
    /**
     * Handle removing from classpath. The goup area is rebuilt if 
     * either <code>forceRebuild</code> is <code>true</code> or 
     * the result contains one element of type <code>IJavaProject</code>.
     * 
     * @param result the result to be selected in the explorer
     * @param forceRebuild <code>true</code> if the hint text group must 
     * be rebuilt, <code>false</code> otherwise.
     */
    private void handleRemoveFromBP(List result, boolean forceRebuild) {
        fPackageExplorer.setSelection(result);
        try {
            if (forceRebuild || containsJavaProject(result)) {
                fActionGroup.refresh(new DialogExplorerActionContext(result, fCurrJProject));
            }
        } catch (JavaModelException e) {
            ExceptionHandler.handle(e, getShell(), NewWizardMessages.HintTextGroup_Exception_Title_refresh, e.getLocalizedMessage()); 
        }
    }
    
    /**
     * Handle output folder editing. This includes:
     * <li>Set the selection of the <code>fPackageExplorer</code>
     * to the only one element contained in the list if this 
     * element is not <code>null</code></li>
     * <li>Add the edited folder to the list of new folders</li>
     *  
     * @param result a list containing only one element of type 
     * <code>CPListElementAttribute</code> which can be <code>null</code>
     */
    private void handleEditOutputFolder(List result) {
        if(result.size() == 0)
            return;
        CPListElementAttribute attribute= (CPListElementAttribute)result.get(0);
        if (attribute != null) {
            fPackageExplorer.setSelection(result);
            IPath path= (IPath)attribute.getValue();
            if (path != null) {
                IFolder folder= fCurrJProject.getProject().getWorkspace().getRoot().getFolder(path);
                if (!folder.exists())
                    fNewFolders.add(folder);
            }
            setOutputLocationFieldText(getOldOutputLocation());
        }
    }

    /**
     * Handle resetting of the Java project to the original 
     * state. This only means that the package explorer 
     * needs to be updated and the selection should 
     * be set to the Java project root itself.
     */
    private void handleResetAll() {
        List list= new ArrayList();
        list.add(fCurrJProject);
        setSelection(list);
    }
    
    /**
     * Find out whether the list contains one element of 
     * type <code>IJavaProject</code>
     * 
     * @param elements the list to be examined
     * @return <code>true</code> if the list contains one element of 
     * type <code>IJavaProject</code>, <code>false</code> otherwise
     */
    private boolean containsJavaProject(List elements) {
        for(int i= 0; i < elements.size(); i++) {
            if (elements.get(i) instanceof IJavaProject)
                return true;
        }
        return false;
    }
    
    private IPath getOldOutputLocation() {
        return new Path(fOutputLocationField.getText()).makeAbsolute();
    }
    
    /**
     * Set the text of <code>fOutputLocationField</code>.
     * 
     * @param oldOutputLocation the project's old output location
     */
    private void setOutputLocationFieldText(IPath oldOutputLocation) {
        try {
            if (!fCurrJProject.getOutputLocation().equals(oldOutputLocation)) {
                fOutputLocationField.setText(fCurrJProject.getOutputLocation().makeRelative().toString());
                IJavaElement element= fCurrJProject.findElement(fCurrJProject.getOutputLocation().removeFirstSegments(1));
                if (element != null)
                    fNewFolders.add(element);
            }
        } catch (JavaModelException exception) {
            ExceptionHandler.handle(exception, getShell(), NewWizardMessages.HintTextGroup_Exception_Title_output, exception.getMessage()); 
        }
    }
    
    /**
     * Return an <code>IOutputFolderQuery</code>.
     * 
     * @see ClasspathModifierQueries#getDefaultFolderQuery(Shell, IPath)
     * @see IClasspathInformationProvider#getOutputFolderQuery()
     */
    public OutputFolderQuery getOutputFolderQuery() {
        return ClasspathModifierQueries.getDefaultFolderQuery(getShell(), new Path(fOutputLocationField.getText()));

    }

    /**
     * Return an <code>IInclusionExclusionQuery</code>.
     * 
     * @see ClasspathModifierQueries#getDefaultInclusionExclusionQuery(Shell)
     * @see IClasspathInformationProvider#getInclusionExclusionQuery()
     */
    public IInclusionExclusionQuery getInclusionExclusionQuery() {
        return ClasspathModifierQueries.getDefaultInclusionExclusionQuery(getShell());
    }

    /**
     * Return an <code>IOutputLocationQuery</code>.
     * @throws JavaModelException 
     * 
     * @see ClasspathModifierQueries#getDefaultOutputLocationQuery(Shell, IPath, List)
     * @see IClasspathInformationProvider#getOutputLocationQuery()
     */
    public IOutputLocationQuery getOutputLocationQuery() throws JavaModelException {
        List classpathEntries= ClasspathModifier.getExistingEntries(fCurrJProject);
        return ClasspathModifierQueries.getDefaultOutputLocationQuery(getShell(), new Path(fOutputLocationField.getText()), classpathEntries);
    }

    /**
     * Return an <code>ILinkToQuery</code>.
     * 
     * @see ClasspathModifierQueries#getDefaultLinkQuery(Shell, IJavaProject, IPath)
     * @see IClasspathInformationProvider#getLinkFolderQuery()
     */
    public ILinkToQuery getLinkFolderQuery() throws JavaModelException {
        return ClasspathModifierQueries.getDefaultLinkQuery(getShell(), fCurrJProject, new Path(fOutputLocationField.getText()));
    }
    
    /**
     * Return an <code>IFolderCreationQuery</code>.
     * 
     * @see ClasspathModifierQueries#getDefaultCreateFolderQuery(Shell, IJavaProject)
     * @see IClasspathInformationProvider#getCreateFolderQuery()
     */
	public ICreateFolderQuery getCreateFolderQuery() throws JavaModelException {
		return ClasspathModifierQueries.getDefaultCreateFolderQuery(getShell(), fCurrJProject);
	}

	/**
	 * Get a query to create a linked source folder.
	 * 
	 * @see IRemoveLinkedFolderQuery
	 * @see descent.internal.corext.buildpath.IClasspathInformationProvider
	 */
	public IRemoveLinkedFolderQuery getRemoveLinkedFolderQuery() throws JavaModelException {
		return ClasspathModifierQueries.getDefaultRemoveLinkedFolderQuery(getShell());
	}

    /**
     * Return an <code>IAddArchivesQuery</code>.
     * 
     * @see ClasspathModifierQueries#getDefaultArchivesQuery(Shell)
     * @see IClasspathInformationProvider#getExternalArchivesQuery()
     */
    public IAddArchivesQuery getExternalArchivesQuery() throws JavaModelException {
        return ClasspathModifierQueries.getDefaultArchivesQuery(getShell());
    }
    
    /**
     * Return an <code>IAddLibrariesQuery</code>.
     * 
     * @see ClasspathModifierQueries#getDefaultLibrariesQuery(Shell)
     * @see IClasspathInformationProvider#getLibrariesQuery()
     */
    public IAddLibrariesQuery getLibrariesQuery() throws JavaModelException {
        return ClasspathModifierQueries.getDefaultLibrariesQuery(getShell());
    }
    
    /**
     * Delete all newly created folders and files.
     * Resources that existed before will not be 
     * deleted.
     */
    public void deleteCreatedResources() {
        Iterator iterator= fNewFolders.iterator();
        while (iterator.hasNext()) {
            Object element= iterator.next();
            IFolder folder;
            try {
                if (element instanceof IFolder)
                    folder= (IFolder)element;
                else if (element instanceof IJavaElement)
                    folder= fCurrJProject.getProject().getWorkspace().getRoot().getFolder(((IJavaElement)element).getPath());
                else {
                    ((IFile)element).delete(false, null);
                    continue;
                }
                folder.delete(false, null);
            } catch (CoreException e) {
            }            
        }
        
        fOutputLocationField.setText(fOldOutputLocation);
        fNewFolders= new ArrayList();
    }

    /**
     * Handle the package explorer action event.
     * This includes:
     * <li>Disposing the old composite which contained the labels with the links</li>
     * <li>Create a new composite</li>
     * <li>Create new labels with the new actions as child of the new composite. The 
     * information which lables have to be created is contained in the event.
     * 
     * @see PackageExplorerActionEvent
     * @see IPackageExplorerActionListener#handlePackageExplorerActionEvent(PackageExplorerActionEvent)
     */
    public void handlePackageExplorerActionEvent(PackageExplorerActionEvent event) {
        // Get the child composite of the top composite
        Composite childComposite= (Composite)fTopComposite.getData();
        
        // Dispose old composite (if necessary)
        if (childComposite != null && childComposite.getParent() != null)
            childComposite.getParent().dispose();
        
        // Create new composite
        ScrolledPageContent spc= new ScrolledPageContent(fTopComposite, SWT.V_SCROLL);
        spc.getVerticalBar().setIncrement(5);
        spc.setLayoutData(new GridData(GridData.FILL_BOTH));
        childComposite= spc.getBody();
        TableWrapLayout tableWrapLayout= new TableWrapLayout();
		tableWrapLayout.leftMargin= 0;
		tableWrapLayout.rightMargin= 0;
		childComposite.setLayout(tableWrapLayout);
        childComposite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
        fTopComposite.setData(childComposite);
        
        // Display available actions
        ClasspathModifierAction[] actions= event.getEnabledActions();
        String[] descriptionText= event.getEnabledActionsText();
        if (noContextHelpAvailable(actions)) {
            String noAction= fActionGroup.getNoActionDescription();
            createFormText(childComposite, Messages.format(NewWizardMessages.HintTextGroup_NoAction, noAction)); 
            fTopComposite.layout(true);
            return;
        }
        
        for (int j= 0; j < ACTION_ORDER.length; j++) {
			for (int i= 0; i < actions.length; i++) {
	            int id= Integer.parseInt(actions[i].getId());
	            if (id == ACTION_ORDER[j]) {
	            	createLabel(childComposite, descriptionText[i], actions[i], fRunnableContext);
	            	break;
	            }
			}
		}
        
        fTopComposite.layout(true);
    }
    
    /**
     * Check if for the current type of selection, no context specific actions can 
     * be applied. Note: this does not mean, that there are NO actions available at all.<p>
     * 
     * For example: if the default package is selected, there is no specific action for this kind 
     * of selection as no operations are allowed on the default package. Nevertheless, the 
     * <code>PackageExplorerActionEvent</code> will return at least one action that allows to 
     * link to an existing folder in the file system, but this operation is always available 
     * and does not add any supporting information to the current selection. Therefore, 
     * it can be filtered and the correct answer to the user is that there is no specific 
     * action for the default package.
     * 
     * @param actions an array of provided actions
     * @return <code>true</code> if there is at least one action that allows context 
     * sensitive operations, <code>false</code> otherwise.
     */
    private boolean noContextHelpAvailable(ClasspathModifierAction[] actions) {
        if (actions.length == 0)
            return true;
        if (actions.length == 1) {
            int id= Integer.parseInt(actions[0].getId());
            if (id == IClasspathInformationProvider.CREATE_LINK)
                return true;
        }
        if (actions.length == 2) {
            int idLink= Integer.parseInt(actions[0].getId());
            int idReset= Integer.parseInt(actions[1].getId());
            if (idReset == IClasspathInformationProvider.RESET_ALL && 
                idLink == IClasspathInformationProvider.CREATE_LINK)
                return true;
        }
        return false;
    }
}
