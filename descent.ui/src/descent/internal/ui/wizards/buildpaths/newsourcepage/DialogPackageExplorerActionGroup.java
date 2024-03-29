/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Matt Chapman, mpchapman@gmail.com - 89977 Make JDT .java agnostic
 *******************************************************************************/

package descent.internal.ui.wizards.buildpaths.newsourcepage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.ToolBar;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;

import org.eclipse.ui.actions.ActionContext;

import descent.core.IClassFile;
import descent.core.ICompilationUnit;
import descent.core.IJavaElement;
import descent.core.IJavaProject;
import descent.core.IPackageFragment;
import descent.core.IPackageFragmentRoot;
import descent.core.JavaCore;
import descent.core.JavaModelException;

import descent.internal.corext.buildpath.AddSelectedSourceFolderOperation;
import descent.internal.corext.buildpath.ClasspathModifier;
import descent.internal.corext.buildpath.ClasspathModifierOperation;
import descent.internal.corext.buildpath.CreateFolderOperation;
import descent.internal.corext.buildpath.EditFiltersOperation;
import descent.internal.corext.buildpath.EditOutputFolderOperation;
import descent.internal.corext.buildpath.ExcludeOperation;
import descent.internal.corext.buildpath.IClasspathInformationProvider;
import descent.internal.corext.buildpath.IPackageExplorerActionListener;
import descent.internal.corext.buildpath.LinkedSourceFolderOperation;
import descent.internal.corext.buildpath.PackageExplorerActionEvent;
import descent.internal.corext.buildpath.RemoveFromClasspathOperation;
import descent.internal.corext.buildpath.ResetAllOperation;
import descent.internal.corext.buildpath.UnexcludeOperation;
import descent.internal.corext.buildpath.ClasspathModifier.IClasspathModifierListener;

import descent.internal.ui.JavaPlugin;
import descent.internal.ui.JavaPluginImages;
import descent.internal.ui.actions.CompositeActionGroup;
import descent.internal.ui.packageview.ClassPathContainer;
import descent.internal.ui.util.ViewerPane;
import descent.internal.ui.wizards.NewWizardMessages;
import descent.internal.ui.wizards.buildpaths.CPListElementAttribute;

/**
 * Action group for the package explorer. Creates and manages a set 
 * of <code>ClasspathModifierOperation</code>s and creates a <code>ToolBarManager</code> 
 * on request. Based on this operations, <code>ClasspathModifierAction</code>s are generated. 
 * The available operations are:
 * 
 * @see descent.internal.corext.buildpath.AddSelectedSourceFolderOperation
 * @see descent.internal.corext.buildpath.RemoveFromClasspathOperation
 * @see descent.internal.corext.buildpath.IncludeOperation
 * @see descent.internal.corext.buildpath.UnincludeOperation
 * @see descent.internal.corext.buildpath.ExcludeOperation
 * @see descent.internal.corext.buildpath.UnexcludeOperation
 * @see descent.internal.corext.buildpath.EditFiltersOperation
 * @see descent.internal.corext.buildpath.ResetOperation
 */
public class DialogPackageExplorerActionGroup extends CompositeActionGroup {
    
    public static class DialogExplorerActionContext extends ActionContext {
        private IJavaProject fJavaProject;
        private List fSelectedElements;

        /**
         * Constructor to create an action context for the dialog package explorer.
         * 
         * For reasons of completeness, the selection of the super class 
         * <code>ActionContext</code> is also set, but is not intendet to be used.
         * 
         * @param selection the current selection
         * @param jProject the element's Java project
         */
        public DialogExplorerActionContext(ISelection selection, IJavaProject jProject) {
            super(null);
            fJavaProject= jProject;
            fSelectedElements= ((IStructuredSelection)selection).toList();
            IStructuredSelection structuredSelection= new StructuredSelection(new Object[] {fSelectedElements, jProject});
            super.setSelection(structuredSelection);
        }
        
        /**
         * Constructor to create an action context for the dialog package explorer.
         * 
         * For reasons of completeness, the selection of the super class 
         * <code>ActionContext</code> is also set, but is not intendet to be used.
         * 
         * @param selectedElements a list of currently selected elements
         * @param jProject the element's Java project
         */
        public DialogExplorerActionContext(List selectedElements, IJavaProject jProject) {
            super(null);
            fJavaProject= jProject;
            fSelectedElements= selectedElements;
            IStructuredSelection structuredSelection= new StructuredSelection(new Object[] {fSelectedElements, jProject});
            super.setSelection(structuredSelection);
        }
        
        public IJavaProject getJavaProject() {
            return fJavaProject;
        }
        
        public List getSelectedElements() {
            return fSelectedElements;
        }
    }
    
    /** Java project */
    public static final int JAVA_PROJECT= 0x01;
    /** Package fragment root */
    public static final int PACKAGE_FRAGMENT_ROOT= 0x02;
    /** Package fragment */
    public static final int PACKAGE_FRAGMENT= 0x03;
    /** Compilation unit */
    public static final int COMPILATION_UNIT= 0x04;
    /** File */
    public static final int FILE= 0x05;
    /** Normal folder */
    public static final int FOLDER= 0x06;
    /** Excluded folder */
    public static final int EXCLUDED_FOLDER= 0x07;
    /** Excluded file */
    public static final int EXCLUDED_FILE= 0x08;
    /** Default output folder */
    public static final int DEFAULT_OUTPUT= 0x09;
    /** Included file */
    public static final int INCLUDED_FILE= 0xA;
    /** Included folder */
    public static final int INCLUDED_FOLDER= 0xB;
    /** Output folder (for a source folder) */
    public static final int OUTPUT= 0xC;
    /** An archive element (.zip or .jar) */
    public static final int ARCHIVE= 0xD;
    /** A IPackageFragmentRoot with include/exclude filters set */
    public static final int MODIFIED_FRAGMENT_ROOT= 0xE;
    /** Default package fragment */
    public static final int DEFAULT_FRAGMENT= 0xF;
    /** Undefined type */
    public static final int UNDEFINED= 0x10;
    /** Multi selection */
    public static final int MULTI= 0x11;
    /** No elements selected */
    public static final int NULL_SELECTION= 0x12;
    /** Elements that are contained in an archive (.jar or .zip) */
    public static final int ARCHIVE_RESOURCE= 0x13;
    /** Elements that represent classpath container (= libraries) */
    public static final int CONTAINER= 0x14;
    
    private ClasspathModifierAction[] fActions;
    private int fLastType;
    private List fListeners;
    private static final int fContextSensitiveActions= 5;
    
    /**
     * Constructor which creates the operations and based on this 
     * operations the actions.
     * 
     * @param provider a information provider to pass necessary information 
     * to the operations
     * @param page a listener for the changes on classpath entries, that is 
     * the listener will be notified whenever a classpath entry changed.
     * @see IClasspathModifierListener
     */
    public DialogPackageExplorerActionGroup(IClasspathInformationProvider provider, final NewSourceContainerWorkbookPage page) {
        super();
        fLastType= UNDEFINED;
        fListeners= new ArrayList();
        fActions= new ClasspathModifierAction[8];
        ClasspathModifierOperation op;
        op= new AddSelectedSourceFolderOperation(page, provider);
        // TODO User disabled image when available
        addAction(new ClasspathModifierAction(op, JavaPluginImages.DESC_ELCL_ADD_AS_SOURCE_FOLDER, null,
                NewWizardMessages.NewSourceContainerWorkbookPage_ToolBar_AddSelSFToCP_label, 
                NewWizardMessages.NewSourceContainerWorkbookPage_ToolBar_AddSelSFToCP_tooltip, IAction.AS_PUSH_BUTTON) {
        	
        	public void run() {
        		page.commitDefaultOutputFolder();
        		super.run();
        	}
        }, 0);
        op= new RemoveFromClasspathOperation(page, provider);
        addAction(new ClasspathModifierAction(op, JavaPluginImages.DESC_ELCL_REMOVE_AS_SOURCE_FOLDER, JavaPluginImages.DESC_DLCL_REMOVE_AS_SOURCE_FOLDER, 
                NewWizardMessages.NewSourceContainerWorkbookPage_ToolBar_RemoveFromCP_label, 
                NewWizardMessages.NewSourceContainerWorkbookPage_ToolBar_RemoveFromCP_tooltip, IAction.AS_PUSH_BUTTON) {
        	
        	public void run() {
        		page.commitDefaultOutputFolder();
        		super.run();
        	}
        }, 1);
        op= new ExcludeOperation(page, provider);
        addAction(new ClasspathModifierAction(op, JavaPluginImages.DESC_ELCL_EXCLUDE_FROM_BUILDPATH, JavaPluginImages.DESC_DLCL_EXCLUDE_FROM_BUILDPATH,
                NewWizardMessages.NewSourceContainerWorkbookPage_ToolBar_Exclude_label, 
                NewWizardMessages.NewSourceContainerWorkbookPage_ToolBar_Exclude_tooltip, IAction.AS_PUSH_BUTTON) {
        	
        	public void run() {
        		page.commitDefaultOutputFolder();
        		super.run();
        	}
        }, 2);
        op= new UnexcludeOperation(page, provider);
        addAction(new ClasspathModifierAction(op, JavaPluginImages.DESC_ELCL_INCLUDE_ON_BUILDPATH, JavaPluginImages.DESC_DLCL_INCLUDE_ON_BUILDPATH,
                NewWizardMessages.NewSourceContainerWorkbookPage_ToolBar_Unexclude_label, 
                NewWizardMessages.NewSourceContainerWorkbookPage_ToolBar_Unexclude_tooltip, IAction.AS_PUSH_BUTTON) {
        	
        	public void run() {
        		page.commitDefaultOutputFolder();
        		super.run();
        	}
        }, 3);
        op= new EditFiltersOperation(page, provider);
        ClasspathModifierAction action= new ClasspathModifierAction(op, JavaPluginImages.DESC_ELCL_CONFIGURE_BUILDPATH_FILTERS, JavaPluginImages.DESC_DLCL_CONFIGURE_BUILDPATH_FILTERS,
                NewWizardMessages.NewSourceContainerWorkbookPage_ToolBar_Edit_label, 
                NewWizardMessages.NewSourceContainerWorkbookPage_ToolBar_Edit_tooltip, IAction.AS_PUSH_BUTTON) {
        	
        	public void run() {
        		page.commitDefaultOutputFolder();
        		super.run();
        	}
        }; 
        ClasspathModifierDropDownAction dropDown= new ClasspathModifierDropDownAction(action, 
                NewWizardMessages.NewSourceContainerWorkbookPage_ToolBar_Configure_label, 
                NewWizardMessages.NewSourceContainerWorkbookPage_ToolBar_Configure_tooltip); 
        addAction(dropDown, 4);
        op= new EditOutputFolderOperation(page, provider);
        action= new ClasspathModifierAction(op, JavaPluginImages.DESC_ELCL_CONFIGURE_OUTPUT_FOLDER, JavaPluginImages.DESC_DLCL_CONFIGURE_OUTPUT_FOLDER,
                NewWizardMessages.NewSourceContainerWorkbookPage_ToolBar_EditOutput_label, 
                NewWizardMessages.NewSourceContainerWorkbookPage_ToolBar_EditOutput_tooltip, IAction.AS_PUSH_BUTTON) {
        	
        	public void run() {
        		page.commitDefaultOutputFolder();
        		super.run();
        	}
        }; 
        dropDown.addAction(action);
        /*addAction(new ClasspathModifierAction(op, JavaPluginImages.DESC_OBJS_TEXT_EDIT, JavaPluginImages.DESC_DLCL_TEXT_EDIT, 
                NewWizardMessages.getString("NewSourceContainerWorkbookPage.ToolBar.Edit.label"), //$NON-NLS-1$
                NewWizardMessages.getString("NewSourceContainerWorkbookPage.ToolBar.Edit.tooltip"), IAction.AS_PUSH_BUTTON), //$NON-NLS-1$
                IClasspathInformationProvider.EDIT);*/
        op= new LinkedSourceFolderOperation(page, provider);
        addAction(new ClasspathModifierAction(op, JavaPluginImages.DESC_ELCL_ADD_LINKED_SOURCE_TO_BUILDPATH, JavaPluginImages.DESC_DLCL_ADD_LINKED_SOURCE_TO_BUILDPATH, 
                NewWizardMessages.NewSourceContainerWorkbookPage_ToolBar_Link_label, 
                NewWizardMessages.NewSourceContainerWorkbookPage_ToolBar_Link_tooltip, IAction.AS_PUSH_BUTTON) {
        	
        	public void run() {
        		page.commitDefaultOutputFolder();
        		super.run();
        	}
        }, 5);
        op= new CreateFolderOperation(page, provider);
        addAction(new ClasspathModifierAction(op, JavaPluginImages.DESC_OBJS_PACKFRAG_ROOT, null, 
        		NewWizardMessages.NewSourceContainerWorkbookPage_ToolBar_CreateSrcFolder_label, NewWizardMessages.NewSourceContainerWorkbookPage_ToolBar_CreateSrcFolder_tooltip
        		, IAction.AS_PUSH_BUTTON){
        	
        	public void run() {
        		page.commitDefaultOutputFolder();
        		super.run();
        	}
        }, 6);
        op= new ResetAllOperation(page, provider);
        addAction(new ClasspathModifierAction(op, JavaPluginImages.DESC_ELCL_CLEAR, JavaPluginImages.DESC_DLCL_CLEAR,
                NewWizardMessages.NewSourceContainerWorkbookPage_ToolBar_ClearAll_label, 
                NewWizardMessages.NewSourceContainerWorkbookPage_ToolBar_ClearAll_tooltip, IAction.AS_PUSH_BUTTON){
        	
        	public void run() {
        		page.commitDefaultOutputFolder();
        		super.run();
        	}
        }, 7);
    }

    private void addAction(ClasspathModifierAction action, int index) {
        fActions[index]= action;
    }
    
    /**
     * Get an action of the specified type
     * 
     * @param type the type of the desired action, must be a
     * constante of <code>IClasspathInformationProvider</code>
     * @return the requested action
     * 
     * @see IClasspathInformationProvider
     */
    public ClasspathModifierAction getAction(int type) {
    	for (int i= 0; i < fActions.length; i++) {
			if (fActions[i].getOperation().getTypeId() == type)
				return fActions[i];
		}
    	throw new ArrayIndexOutOfBoundsException();
    }
    
    public ClasspathModifierAction[] getActions() {
    	List result= new ArrayList();
    	for (int i= 0; i < fActions.length; i++) {
			ClasspathModifierAction action= fActions[i];
			if (action instanceof ClasspathModifierDropDownAction) {
				ClasspathModifierDropDownAction dropDownAction= (ClasspathModifierDropDownAction)action;
				ClasspathModifierAction[] actions= dropDownAction.getActions();
				for (int j= 0; j < actions.length; j++) {
					result.add(actions[j]);
				}
			} else {
				result.add(action);
			}
		}
    	return (ClasspathModifierAction[])result.toArray(new ClasspathModifierAction[result.size()]);
    }
    
    /**
     * Method that is called whenever setting of 
     * output folders is allowed or forbidden (for example 
     * on changing a checkbox with this setting):
     * 
     * @param showOutputFolders <code>true</code> if output 
     * folders should be shown, <code>false</code> otherwise.
     */
    public void showOutputFolders(boolean showOutputFolders) {
        ClasspathModifierDropDownAction action= (ClasspathModifierDropDownAction)getAction(IClasspathInformationProvider.EDIT_FILTERS);
        EditOutputFolderOperation operation= (EditOutputFolderOperation)action.getActions()[1].getOperation();
        operation.showOutputFolders(showOutputFolders);
    }
    
    /**
     * Create a toolbar manager for a given 
     * <code>ViewerPane</code>
     * 
     * @param pane the pane to create the <code>
     * ToolBarManager</code> for.
     * @return the created <code>ToolBarManager</code>
     */
    public ToolBarManager createLeftToolBarManager(ViewerPane pane) {
        ToolBarManager tbm= pane.getToolBarManager();
        for (int i= 0; i < fContextSensitiveActions; i++) {
            tbm.add(fActions[i]);
            if (i == 1 || i == 3)
                tbm.add(new Separator());
        }
        tbm.update(true);
        return tbm;
    }
    
    /**
     * Create a toolbar manager for a given 
     * <code>ViewerPane</code>
     * 
     * @param pane the pane to create the help toolbar for
     * @return the created <code>ToolBarManager</code>
     */
    public ToolBarManager createLeftToolBar(ViewerPane pane) {
        ToolBar tb= new ToolBar(pane, SWT.FLAT);
        pane.setTopRight(tb);
        ToolBarManager tbm= new ToolBarManager(tb);
        for (int i= fContextSensitiveActions; i < fActions.length; i++) {
        	tbm.add(fActions[i]);
        }
        tbm.add(new HelpAction());
        tbm.update(true);
        return tbm;
    }
    
    /**
     * Forces the action group to recompute the available actions 
     * and fire an event to all listeners
     * @throws JavaModelException 
     * 
     * @see #setContext(DialogExplorerActionContext)
     * @see #informListeners(String[], ClasspathModifierAction[])
     */
    public void refresh(DialogExplorerActionContext context) throws JavaModelException {
        super.setContext(context);
        if (context == null) // can happen when disposing
            return;
        List selectedElements= context.getSelectedElements();
        IJavaProject project= context.getJavaProject();
        
        int type= MULTI;
        if (selectedElements.size() == 0) {
            type= NULL_SELECTION;
            
            if (type == fLastType)
                return;
        } else if (selectedElements.size() == 1 || identicalTypes(selectedElements, project)) {
            type= getType(selectedElements.get(0), project);
        }
        
        internalSetContext(selectedElements, project, type);
    }
    
    /**
     * Set the context of the action group. Note that this method is deprecated. 
     * <ul><li>Clients should use DialogPackageExplorerActionGroup.setContext(DialogExplorerActionContext) instead</li>
     * <li>If this method is called, it is expected that the provided context is of type 
     * <code>DialogExplorerActionContext</code>. If this is not the case, the caller will 
     * end up with a <code>ClassCastException</code>.
     * 
     * @deprecated use instead DialogPackageExplorerActionGroup.setContext(DialogExplorerActionContext)
     * 
     * @see #setContext(DialogExplorerActionContext)
     */
    public void setContext(ActionContext context) {
        try {
            setContext((DialogExplorerActionContext)context);
        } catch (JavaModelException e) {
            JavaPlugin.log(e);
        }
    }
    
    /**
     * Set the context for the action group. This also includes 
     * updating the actions (that is, enable or disable them). 
     * The decision which actions should be enabled or disabled is based 
     * on the content of the <code>DialogExplorerActionContext</code>
     * 
     * If the type of the selection changes, then listeners will be notified 
     * about the new set of available actions.
     * 
     * Note: notification is only done if the TYPE changes (not the selected object 
     * as such). This means that if elements of the same type are selected (for 
     * example two times a folder), NO notification will take place. There might 
     * be situations where the type of two objects is the same but the set of 
     * available actions is not. However, if clients decide that upon some action 
     * a recomputation of the available actions has to be forced, then 
     * <code>PackageExplorerActionGroup.refresh(DialogExplorerActionContext)</code> can be 
     * called.
     * 
     * @param context the action context
     * 
     * @see IPackageExplorerActionListener
     * @see PackageExplorerActionEvent
     * @see DialogExplorerActionContext
     * @see #addListener(IPackageExplorerActionListener)
     * @see #refresh(DialogExplorerActionContext)
     * 
     * @throws JavaModelException if there is a failure while computing the available 
     * actions.
     */
    public void setContext(DialogExplorerActionContext context) throws JavaModelException {
        super.setContext(context);
        if (context == null) // can happen when disposing
            return;
        List selectedElements= context.getSelectedElements();
        IJavaProject project= context.getJavaProject();
        
        int type= MULTI;
        if (selectedElements.size() == 0) {
            type= NULL_SELECTION;
            
            if (type == fLastType)
                return;
        }
        else if (selectedElements.size() == 1 || identicalTypes(selectedElements, project)) {
            type= getType(selectedElements.get(0), project);
            
            if (selectedElements.size() > 1)
                type= type | MULTI;
        
            if (type == fLastType)
                return;
        }
        
        internalSetContext(selectedElements, project, type);
    }
    
    /**
     * Get a description for the last selection explaining 
     * why no operation is possible.<p>
     * This can be usefull if a context sensitive widget does 
     * not want to display all operations although some of them 
     * are valid.
     * 
     * @return a description for the last selection that explains 
     * why no operation is available.
     */
    public String getNoActionDescription() {
        String[] description= noAction(fLastType);
        return description[0];
    }
    
    /**
     * Internal method to set the context of the action group.
     * 
     * @param selectedElements a list of selected elements, can be empty
     * @param project the Java project
     * @param type the type of the selected element(s)
     * @throws JavaModelException
     */
    private void internalSetContext(List selectedElements, IJavaProject project, int type) throws JavaModelException {
        fLastType= type;
        List availableActions= getAvailableActions(selectedElements, project);
        ClasspathModifierAction[] actions= new ClasspathModifierAction[availableActions.size()];
        String[] descriptions= new String[availableActions.size()];
        if (availableActions.size() > 0) {
            for(int i= 0; i < availableActions.size(); i++) {
                ClasspathModifierAction action= (ClasspathModifierAction)availableActions.get(i);
                actions[i]= action;
                descriptions[i]= action.getDescription(type);
            }
        } else
            descriptions= noAction(type);

        informListeners(descriptions, actions);
    }
    
    /**
     * Finds out wheter the list of elements consists only of elements 
     * having the same type (for example all are of type 
     * DialogPackageExplorerActionGroup.COMPILATION_UNIT). This allows 
     * to use a description for the available actions which is more 
     * specific and therefore provides more information.
     * 
     * @param elements a list of elements to be compared to each other
     * @param project the java project
     * @return <code>true</code> if all elements are of the same type, 
     * <code>false</code> otherwise.
     * @throws JavaModelException 
     */
    private boolean identicalTypes(List elements, IJavaProject project) throws JavaModelException {
		if (elements.size() == 0) {
			return false;
		}
		
        Object firstElement= elements.get(0);
        int firstType= getType(firstElement, project);
        for(int i= 1; i < elements.size(); i++) {
            if(firstType != getType(elements.get(i), project))
                return false;
        }
        return true;
    }
    
    /**
     * Inform all listeners about new actions.
     * 
     * @param descriptions an array of descriptions for each 
     * actions, where the description at position 'i' belongs to 
     * the action at position 'i'
     * @param actions an array of available actions
     */
    private void informListeners(String[] descriptions, ClasspathModifierAction[] actions) {
        Iterator iterator= fListeners.iterator();
        PackageExplorerActionEvent event= new PackageExplorerActionEvent(descriptions, actions);
        while(iterator.hasNext()) {
            IPackageExplorerActionListener listener= (IPackageExplorerActionListener)iterator.next();
            listener.handlePackageExplorerActionEvent(event);
        }
    }
    
    /**
     * Returns string array with only one element which contains a short reason to indicate 
     * why there are no actions available.
     * 
     * @return a description to explain why there are no actions available
     */
    private String[] noAction(int type) {
        String reason;
        switch(type) {
            case FILE: reason= NewWizardMessages.PackageExplorerActionGroup_NoAction_File; break; 
            case FILE | MULTI: reason= NewWizardMessages.PackageExplorerActionGroup_NoAction_File; break; 
            case DEFAULT_FRAGMENT: reason= NewWizardMessages.PackageExplorerActionGroup_NoAction_DefaultPackage; break; 
            case DEFAULT_FRAGMENT | MULTI: reason= NewWizardMessages.PackageExplorerActionGroup_NoAction_DefaultPackage; break; 
            case NULL_SELECTION: reason= NewWizardMessages.PackageExplorerActionGroup_NoAction_NullSelection; break; 
            case MULTI: reason= NewWizardMessages.PackageExplorerActionGroup_NoAction_MultiSelection; break; 
            case ARCHIVE_RESOURCE: reason= NewWizardMessages.PackageExplorerActionGroup_NoAction_ArchiveResource; break; 
            default: reason= NewWizardMessages.PackageExplorerActionGroup_NoAction_NoReason; 
        }
        return new String[] {reason};
    }  
    
    /**
     * Computes the type based on the current selection. The type
     * can be usefull to set the content of the hint text group
     * properly.
     * 
     * @param obj the object to get the type from
     * @return the type of the current selection or UNDEFINED if no
     * appropriate type could be found. Possible types are:<br>
     * PackageExplorerActionGroup.FOLDER<br>
     * PackageExplorerActionGroup.EXCLUDED_FOLDER;<br>
     * PackageExplorerActionGroup.EXCLUDED_FILE<br>
     * PackageExplorerActionGroup.DEFAULT_OUTPUT<br>
     * PackageExplorerActionGroup.INCLUDED_FILE<br>
     * PackageExplorerActionGroup.INCLUDED_FOLDER<br>
     * PackageExplorerActionGroup.OUTPUT<br>
     * PackageExplorerActionGroup.MODIFIED_FRAGMENT_ROOT<br>
     * PackageExplorerActionGroup.DEFAULT_FRAGMENT<br>
     * PackageExplorerActionGroup.JAVA_PROJECT<br>
     * PackageExplorerActionGroup.PACKAGE_FRAGMENT_ROOT<br>
     * PackageExplorerActionGroup.PACKAGE_FRAGMENT<br>
     * PackageExplorerActionGroup.COMPILATION_UNIT<br>
     * PackageExplorerActionGroup.FILE<br>
     * @throws JavaModelException 
     */
    public static int getType(Object obj, IJavaProject project) throws JavaModelException {
            if (obj instanceof IJavaProject)
                return JAVA_PROJECT;
            if (obj instanceof ClassPathContainer)
                return CONTAINER;
            if (obj instanceof IPackageFragmentRoot)
                return ClasspathModifier.filtersSet((IPackageFragmentRoot)obj) ? MODIFIED_FRAGMENT_ROOT : PACKAGE_FRAGMENT_ROOT;
            if (obj instanceof IPackageFragment) {
                if (ClasspathModifier.isDefaultFragment((IPackageFragment)obj)) {
                    if (((IPackageFragmentRoot)((IJavaElement)obj).getAncestor(IJavaElement.PACKAGE_FRAGMENT_ROOT)).isArchive())
                        return ARCHIVE_RESOURCE;
                    return DEFAULT_FRAGMENT;
                }
                if (ClasspathModifier.isIncluded((IJavaElement)obj, project, null))
                    return INCLUDED_FOLDER;
                if (((IPackageFragmentRoot)((IJavaElement)obj).getAncestor(IJavaElement.PACKAGE_FRAGMENT_ROOT)).isArchive())
                    return ARCHIVE_RESOURCE;
                return PACKAGE_FRAGMENT;
            }
            if (obj instanceof ICompilationUnit) {
                if (((IPackageFragmentRoot)((IJavaElement)obj).getAncestor(IJavaElement.PACKAGE_FRAGMENT_ROOT)).isArchive())
                    return ARCHIVE_RESOURCE;
                return ClasspathModifier.isIncluded((IJavaElement)obj, project, null) ? INCLUDED_FILE : COMPILATION_UNIT;
            }
            if (obj instanceof IFolder) {
                return getFolderType((IFolder)obj, project);
            }
            if (obj instanceof IFile)
                return getFileType((IFile)obj, project);
            if (obj instanceof IClassFile)
                return FILE;
            if (obj instanceof CPListElementAttribute)
                return ClasspathModifier.isDefaultOutputFolder((CPListElementAttribute) obj) ? DEFAULT_OUTPUT : OUTPUT;
        return UNDEFINED;
    }
    
    /**
     * Get the type of the folder
     * 
     * @param folder folder to get the type from
     * @return the type code for the folder. Possible types are:<br>
     * PackageExplorerActionGroup.FOLDER<br>
     * PackageExplorerActionGroup.EXCLUDED_FOLDER;<br>
     * @throws JavaModelException 
     */
    private static int getFolderType(IFolder folder, IJavaProject project) throws JavaModelException {
        IContainer folderParent= folder.getParent();
		if (folderParent.getFullPath().equals(project.getPath()))
            return FOLDER;
        if (ClasspathModifier.getFragment(folderParent) != null)
            return EXCLUDED_FOLDER;
        IPackageFragmentRoot fragmentRoot= ClasspathModifier.getFragmentRoot(folder, project, null);
		if (fragmentRoot == null)
            return FOLDER;
        if (fragmentRoot.equals(JavaCore.create(folderParent)))
            return EXCLUDED_FOLDER;
        return FOLDER;              
    }
    
    /**
     * Get the type of the file
     * 
     * @param file file to get the type from
     * @return the type code for the file. Possible types are:<br>
     * PackageExplorerActionGroup.EXCLUDED_FILE<br>
     * PackageExplorerActionGroup.FILE
     * @throws JavaModelException 
     */
    private static int getFileType(IFile file, IJavaProject project) throws JavaModelException {
        if (ClasspathModifier.isArchive(file, project))
            return ARCHIVE;
        if (!JavaCore.isJavaLikeFileName(file.getName()))
            return FILE;
        IContainer fileParent= file.getParent();
		if (fileParent.getFullPath().equals(project.getPath())) {
            if (project.isOnClasspath(project)) 
                return EXCLUDED_FILE;
            return FILE;
        }
        IPackageFragmentRoot fragmentRoot= ClasspathModifier.getFragmentRoot(file, project, null);
		if (fragmentRoot == null)
            return FILE;
        if (fragmentRoot.isArchive())
            return ARCHIVE_RESOURCE;
        if (fragmentRoot.equals(JavaCore.create(fileParent)))
            return EXCLUDED_FILE;
        if (ClasspathModifier.getFragment(fileParent) == null) {
            if (ClasspathModifier.parentExcluded(fileParent, project))
                return FILE;
            return EXCLUDED_FILE;
        }
        return EXCLUDED_FILE;
    }
    
    /**
     * Based on the given list of elements, get the list of available 
     * actions that can be applied on this elements
     * 
     * @param selectedElements the list of elements to get the actions for
     * @param project the Java project
     * @return a list of <code>ClasspathModifierAction</code>s
     * @throws JavaModelException
     */
    private List getAvailableActions(List selectedElements, IJavaProject project) throws JavaModelException {
		if (project == null || !project.exists()) {
			return new ArrayList();
		}
		
        List actions= new ArrayList();
        int[] types= new int[selectedElements.size()];
        for(int i= 0; i < types.length; i++) {
            types[i]= getType(selectedElements.get(i), project);
        }
        for(int i= 0; i < fActions.length; i++) {
            if(fActions[i] instanceof ClasspathModifierDropDownAction) {
                if(changeEnableState(fActions[i], selectedElements, types)) {
                    ClasspathModifierAction[] dropDownActions= ((ClasspathModifierDropDownAction)fActions[i]).getActions();
                    for(int j= 0; j < dropDownActions.length; j++) {
                        if(changeEnableState(dropDownActions[j], selectedElements, types))
                            actions.add(dropDownActions[j]);
                    }
                }
            }
            else if(changeEnableState(fActions[i], selectedElements, types)) {
                actions.add(fActions[i]);
            }
        }
        return actions;
    }
    
    /**
     * Changes the enabled state of an action if necessary.
     * 
     * @param action the action to change it's state for
     * @param selectedElements a list of selected elements
     * @param types an array of types corresponding to the types of 
     * the selected elements 
     * @return <code>true</code> if the action is valid (= enabled), <code>false</code> otherwise
     * @throws JavaModelException
     */
    private boolean changeEnableState(ClasspathModifierAction action, List selectedElements, int[] types) throws JavaModelException {
        if(action.isValid(selectedElements, types)) {
            if (!action.isEnabled())
                action.setEnabled(true);
            return true;
        } else {
            if (action.isEnabled())
                action.setEnabled(false);
            return false;
        }
    }
    
    /**
     * Fill the context menu with the available actions
     * 
     * @param menu the menu to be filled up with actions
     */
    public void fillContextMenu(IMenuManager menu) {        
        for (int i= 0; i < fContextSensitiveActions; i++) {
            IAction action= getAction(i);
            if (action instanceof ClasspathModifierDropDownAction) {
                if (action.isEnabled()) {
                    IAction[] actions= ((ClasspathModifierDropDownAction)action).getActions();
                    for(int j= 0; j < actions.length; j++) {
                        if(actions[j].isEnabled())
                            menu.add(actions[j]);
                    }
                }
            }
            else if (action.isEnabled())
                menu.add(action);
        }
        super.fillContextMenu(menu);
    }
    
    /**
     * Add listeners for the <code>PackageExplorerActionEvent</code>.
     * 
     * @param listener the listener to be added
     * 
     * @see PackageExplorerActionEvent
     * @see IPackageExplorerActionListener
     */
    public void addListener(IPackageExplorerActionListener listener) {
        fListeners.add(listener);
    }
    
    /**
     * Remove the listener from the list of registered listeners.
     * 
     * @param listener the listener to be removed
     */
    public void removeListener(IPackageExplorerActionListener listener) {
        fListeners.remove(listener);
    }
    
    /* (non-Javadoc)
     * @see descent.internal.ui.actions.CompositeActionGroup#dispose()
     */
    public void dispose() {
        fListeners.clear();
        super.dispose();
    }
}
