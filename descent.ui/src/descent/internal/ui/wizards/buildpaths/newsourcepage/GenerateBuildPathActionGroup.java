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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.texteditor.IUpdate;

import descent.core.IClasspathEntry;
import descent.core.IJavaElement;
import descent.core.IJavaProject;
import descent.core.IPackageFragmentRoot;
import descent.core.JavaModelException;
import descent.internal.corext.buildpath.ClasspathModifier;
import descent.internal.ui.IJavaHelpContextIds;
import descent.internal.ui.JavaPluginImages;
import descent.internal.ui.actions.ActionMessages;
import descent.internal.ui.wizards.NewWizardMessages;
import descent.internal.ui.wizards.buildpaths.AddSourceFolderWizard;
import descent.internal.ui.wizards.buildpaths.CPListElement;
import descent.internal.ui.wizards.buildpaths.EditFilterWizard;
import descent.ui.IContextMenuConstants;
import descent.ui.PreferenceConstants;
import descent.ui.actions.AbstractOpenWizardAction;

/**
 * Action group that adds the source and generate actions to a part's context
 * menu and installs handlers for the corresponding global menu actions.
 * 
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 * 
 * @since 3.1
 */
public class GenerateBuildPathActionGroup extends ActionGroup {
    /**
     * Pop-up menu: id of the source sub menu (value <code>descent.ui.buildpath.menu</code>).
     * 
     * @since 3.1
     */
    public static final String MENU_ID= "descent.ui.buildpath.menu"; //$NON-NLS-1$
    
    /**
     * Pop-up menu: id of the build path (add /remove) group of the build path sub menu (value
     * <code>buildpathGroup</code>).
     * 
     * @since 3.1
     */
    public static final String GROUP_BUILDPATH= "buildpathGroup";  //$NON-NLS-1$
    
    /**
     * Pop-up menu: id of the filter (include / exclude) group of the build path sub menu (value
     * <code>filterGroup</code>).
     * 
     * @since 3.1
     */
    public static final String GROUP_FILTER= "filterGroup";  //$NON-NLS-1$
    
    /**
     * Pop-up menu: id of the customize (filters / output folder) group of the build path sub menu (value
     * <code>customizeGroup</code>).
     * 
     * @since 3.1
     */
    public static final String GROUP_CUSTOMIZE= "customizeGroup";  //$NON-NLS-1$
    
	private static class NoActionAvailable extends Action {
		public NoActionAvailable() {
			setEnabled(false);
			setText(NewWizardMessages.GenerateBuildPathActionGroup_no_action_available); 
		}
	}
	private Action fNoActionAvailable= new NoActionAvailable();
	   
    private static abstract class OpenBuildPathWizardAction extends AbstractOpenWizardAction implements ISelectionChangedListener {
    	
    	protected IPath getOutputLocation(IJavaProject javaProject) {
    		try {
    			return javaProject.getOutputLocation();		
    		} catch (CoreException e) {
    			IProject project= javaProject.getProject();
    			IPath projPath= project.getFullPath();
    			return projPath.append(PreferenceConstants.getPreferenceStore().getString(PreferenceConstants.SRCBIN_BINNAME));
    		}
    	}
    	
		/**
		 * {@inheritDoc}
		 */
		public void selectionChanged(SelectionChangedEvent event) {
            ISelection selection = event.getSelection();
            if (selection instanceof IStructuredSelection) {
    			setEnabled(selectionChanged((IStructuredSelection) selection));
            } else {
    			setEnabled(selectionChanged(StructuredSelection.EMPTY));
            }
		}

		//Needs to be public for the operation, will be protected later.
		public abstract boolean selectionChanged(IStructuredSelection selection);
    }
    
    private abstract static class CreateSourceFolderAction extends OpenBuildPathWizardAction {

		private AddSourceFolderWizard fAddSourceFolderWizard;
		private IJavaProject fSelectedProject;
		private final boolean fIsLinked;
		
		public CreateSourceFolderAction(boolean isLinked) {
			fIsLinked= isLinked;
		}

		/**
		 * {@inheritDoc}
		 */
		protected INewWizard createWizard() throws CoreException {
			CPListElement newEntrie= new CPListElement(fSelectedProject, IClasspathEntry.CPE_SOURCE);
			CPListElement[] existing= CPListElement.createFromExisting(fSelectedProject);
			boolean isProjectSrcFolder= CPListElement.isProjectSourceFolder(existing, fSelectedProject);
			fAddSourceFolderWizard= new AddSourceFolderWizard(existing, newEntrie, getOutputLocation(fSelectedProject), fIsLinked, false, false, isProjectSrcFolder, isProjectSrcFolder);
			return fAddSourceFolderWizard;
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean selectionChanged(IStructuredSelection selection) {
			if (selection.size() == 1 && selection.getFirstElement() instanceof IJavaProject) {
				fSelectedProject= (IJavaProject)selection.getFirstElement();
				return true;
			}
			return false;
		}

		public List getCPListElements() {
			return fAddSourceFolderWizard.getExistingEntries();
		}
    	
    }
		
    public static class CreateLocalSourceFolderAction extends CreateSourceFolderAction {

		public CreateLocalSourceFolderAction() {
			super(false);
			setText(ActionMessages.OpenNewSourceFolderWizardAction_text2); 
    		setDescription(ActionMessages.OpenNewSourceFolderWizardAction_description); 
    		setToolTipText(ActionMessages.OpenNewSourceFolderWizardAction_tooltip); 
    		setImageDescriptor(JavaPluginImages.DESC_TOOL_NEWPACKROOT);
    		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IJavaHelpContextIds.OPEN_SOURCEFOLDER_WIZARD_ACTION);
    	}
    }
    
    public static class CreateLinkedSourceFolderAction extends CreateSourceFolderAction {
    	
    	public CreateLinkedSourceFolderAction() {
    		super(true);
			setText(NewWizardMessages.NewSourceContainerWorkbookPage_ToolBar_Link_label); 
    		setToolTipText(NewWizardMessages.NewSourceContainerWorkbookPage_ToolBar_Link_tooltip);
    		setImageDescriptor(JavaPluginImages.DESC_ELCL_ADD_LINKED_SOURCE_TO_BUILDPATH);
    		setDescription(NewWizardMessages.PackageExplorerActionGroup_FormText_createLinkedFolder);
    	}
    }
    
    public static class EditFilterAction extends OpenBuildPathWizardAction {
    	
    	private IJavaProject fSelectedProject;
    	private IJavaElement fSelectedElement;
		private EditFilterWizard fEditFilterWizard;
    	
		public EditFilterAction() {
    		setText(NewWizardMessages.NewSourceContainerWorkbookPage_ToolBar_Edit_label); 
    		setDescription(NewWizardMessages.PackageExplorerActionGroup_FormText_Edit);
    		setToolTipText(NewWizardMessages.NewSourceContainerWorkbookPage_ToolBar_Edit_tooltip); 
    		setImageDescriptor(JavaPluginImages.DESC_ELCL_CONFIGURE_BUILDPATH_FILTERS);
    		setDisabledImageDescriptor(JavaPluginImages.DESC_DLCL_CONFIGURE_BUILDPATH_FILTERS);
    	}

		/**
		 * {@inheritDoc}
		 */
		protected INewWizard createWizard() throws CoreException {
			CPListElement[] existingEntries= CPListElement.createFromExisting(fSelectedProject);
			CPListElement elementToEdit= findElement(fSelectedElement, existingEntries);
			fEditFilterWizard= new EditFilterWizard(existingEntries, elementToEdit, getOutputLocation(fSelectedProject));
			return fEditFilterWizard;
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean selectionChanged(IStructuredSelection selection) {
			if (selection.size() != 1)
				return false;
			
			try {
				Object element= selection.getFirstElement();
				if (element instanceof IJavaProject) {
					IJavaProject project= (IJavaProject)element;	
					if (ClasspathModifier.isSourceFolder(project)) {
						fSelectedProject= project;
						fSelectedElement= (IJavaElement)element;
						return true;
					}
				} else if (element instanceof IPackageFragmentRoot) {
					IPackageFragmentRoot packageFragmentRoot= ((IPackageFragmentRoot) element);
					IJavaProject project= packageFragmentRoot.getJavaProject();
					if (packageFragmentRoot.getKind() == IPackageFragmentRoot.K_SOURCE && project != null) {
						fSelectedProject= project;
						fSelectedElement= (IJavaElement)element;
						return true;
					}
				}
			} catch (JavaModelException e) {
				return false;
			}
			return false;
		}

		private static CPListElement findElement(IJavaElement element, CPListElement[] elements) {
			IPath path= element.getPath();
    		for (int i= 0; i < elements.length; i++) {
				CPListElement cur= elements[i];
				if (cur.getEntryKind() == IClasspathEntry.CPE_SOURCE && cur.getPath().equals(path)) {
					return cur;
				}
			}
    		return null;
		}

		public List getCPListElements() {
			return fEditFilterWizard.getExistingEntries();
		}
    }

    /* TODO JDT UI jar
    private class UpdateJarFileAction extends JarImportWizardAction implements IUpdate {

		public UpdateJarFileAction() {
			setText(ActionMessages.GenerateBuildPathActionGroup_update_jar_text);
			setDescription(ActionMessages.GenerateBuildPathActionGroup_update_jar_description);
			setToolTipText(ActionMessages.GenerateBuildPathActionGroup_update_jar_tooltip);
			setImageDescriptor(JavaPluginImages.DESC_OBJS_JAR);
			PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IJavaHelpContextIds.JARIMPORT_WIZARD_PAGE);
		}
		
		public void update() {
			final IWorkbenchPart part= fSite.getPage().getActivePart();
			if (part != null)
				setActivePart(this, part);
			selectionChanged(this, fSite.getSelectionProvider().getSelection());
		}
	}
	*/

    private IWorkbenchSite fSite;
    private List/*<Action>*/ fActions;

	private String fGroupName= IContextMenuConstants.GROUP_REORGANIZE;
        
    /**
     * Creates a new <code>GenerateActionGroup</code>. The group 
     * requires that the selection provided by the page's selection provider 
     * is of type <code>org.eclipse.jface.viewers.IStructuredSelection</code>.
     * 
     * @param page the page that owns this action group
     */
    public GenerateBuildPathActionGroup(Page page) {
        this(page.getSite());
    }
    
    /**
     * Creates a new <code>GenerateActionGroup</code>. The group 
     * requires that the selection provided by the part's selection provider 
     * is of type <code>org.eclipse.jface.viewers.IStructuredSelection</code>.
     * 
     * @param part the view part that owns this action group
     */
    public GenerateBuildPathActionGroup(IViewPart part) {
        this(part.getSite());
    }
    
    private GenerateBuildPathActionGroup(IWorkbenchSite site) {
        fSite= site;
        fActions= new ArrayList();
        
		final CreateLinkedSourceFolderAction addLinkedSourceFolderAction= new CreateLinkedSourceFolderAction();
		fActions.add(addLinkedSourceFolderAction);
        
        final CreateLocalSourceFolderAction addSourceFolderAction= new CreateLocalSourceFolderAction();
        fActions.add(addSourceFolderAction);
        
		final AddFolderToBuildpathAction addFolder= new AddFolderToBuildpathAction(site);
		fActions.add(addFolder);
		
		final AddSelectedLibraryToBuildpathAction addSelectedLibrary= new AddSelectedLibraryToBuildpathAction(site);
		fActions.add(addSelectedLibrary);
	
		final RemoveFromBuildpathAction remove= new RemoveFromBuildpathAction(site);
		fActions.add(remove);
		
		final AddArchiveToBuildpathAction addArchive= new AddArchiveToBuildpathAction(site);
		fActions.add(addArchive);
		
		final AddLibraryToBuildpathAction addLibrary= new AddLibraryToBuildpathAction(site);
		fActions.add(addLibrary);	
		
		/* TODO JDT UI jar
		final UpdateJarFileAction updateAction= new UpdateJarFileAction();
		fActions.add(updateAction);
		*/
		
		final ExcludeFromBuildpathAction exclude= new ExcludeFromBuildpathAction(site);
		fActions.add(exclude);	
		
		final IncludeToBuildpathAction include= new IncludeToBuildpathAction(site);
		fActions.add(include);		

		final EditFilterAction editFilterAction= new EditFilterAction();
		fActions.add(editFilterAction);
			
		final EditOutputFolderAction editOutput= new EditOutputFolderAction(site);
		fActions.add(editOutput);
		
		final ConfigureBuildPathAction configure= new ConfigureBuildPathAction(site);
		fActions.add(configure);
		
		final ISelectionProvider provider= fSite.getSelectionProvider();
		for (Iterator iter= fActions.iterator(); iter.hasNext();) {
			Action action= (Action)iter.next();
			if (action instanceof ISelectionChangedListener) {
				provider.addSelectionChangedListener((ISelectionChangedListener)action);
			}
		}
		
    }
            
    /* (non-Javadoc)
     * Method declared in ActionGroup
     */
    public void fillActionBars(IActionBars actionBar) {
        super.fillActionBars(actionBar);
        setGlobalActionHandlers(actionBar);
    }
    
    /* (non-Javadoc)
     * Method declared in ActionGroup
     */
    public void fillContextMenu(IMenuManager menu) {
        super.fillContextMenu(menu);
        if (!canOperateOnSelection())
        	return;
        String menuText= ActionMessages.BuildPath_label;
        IMenuManager subMenu= new MenuManager(menuText, MENU_ID);
        subMenu.addMenuListener(new IMenuListener() {
        	public void menuAboutToShow(IMenuManager manager) {
        		fillViewSubMenu(manager);
        	}
        });
        subMenu.setRemoveAllWhenShown(true);
        subMenu.add(new ConfigureBuildPathAction(fSite));
        menu.appendToGroup(fGroupName, subMenu);
    }
        
	private void fillViewSubMenu(IMenuManager source) {
        int added= 0;
        int i=0;
        for (Iterator iter= fActions.iterator(); iter.hasNext();) {
			Action action= (Action)iter.next();
			if (action instanceof IUpdate)
				((IUpdate) action).update();
			
            if (i == 2)
                source.add(new Separator(GROUP_BUILDPATH));
            else if (i == 8)
                source.add(new Separator(GROUP_FILTER));
            else if (i == 10)
                source.add(new Separator(GROUP_CUSTOMIZE));
            added+= addAction(source, action);
            i++;
		}

        if (added == 0) {
        	source.add(fNoActionAvailable);
        }
    }
        
    private void setGlobalActionHandlers(IActionBars actionBar) {
        // TODO implement
    }
    
    private int addAction(IMenuManager menu, IAction action) {
        if (action != null && action.isEnabled()) {
            menu.add(action);
            return 1;
        }
        return 0;
    }
    
    private boolean canOperateOnSelection() {
    	ISelection sel= fSite.getSelectionProvider().getSelection();
    	if (!(sel instanceof IStructuredSelection))
    		return false;
    	IStructuredSelection selection= (IStructuredSelection)sel;
    	for (Iterator iter= selection.iterator(); iter.hasNext();) {
			Object element= iter.next();
			if (element instanceof IWorkingSet)
				return false;
		}
    	return true;
    }

	/**
	 * {@inheritDoc}
	 */
	public void dispose() {
		if (fActions != null) {
			final ISelectionProvider provider= fSite.getSelectionProvider();
			for (Iterator iter= fActions.iterator(); iter.hasNext();) {
				Action action= (Action)iter.next();
				if (action instanceof ISelectionChangedListener)
					provider.removeSelectionChangedListener((ISelectionChangedListener) action);
			}
		}
		fActions= null;
		super.dispose();
	}
}
