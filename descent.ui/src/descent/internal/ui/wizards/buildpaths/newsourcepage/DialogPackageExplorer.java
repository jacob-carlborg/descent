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

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

import descent.core.IClasspathEntry;
import descent.core.IJavaProject;
import descent.core.IPackageFragmentRoot;
import descent.core.JavaModelException;

import descent.internal.corext.buildpath.ClasspathModifier;
import descent.internal.corext.buildpath.IClasspathInformationProvider;
import descent.internal.corext.util.Messages;

import descent.ui.JavaElementLabels;
import descent.ui.JavaElementSorter;
import descent.ui.StandardJavaElementContentProvider;

import descent.internal.ui.JavaPlugin;
import descent.internal.ui.filters.LibraryFilter;
import descent.internal.ui.filters.OutputFolderFilter;
import descent.internal.ui.viewsupport.AppearanceAwareLabelProvider;
import descent.internal.ui.viewsupport.DecoratingJavaLabelProvider;
import descent.internal.ui.viewsupport.JavaElementImageProvider;
import descent.internal.ui.wizards.NewWizardMessages;
import descent.internal.ui.wizards.buildpaths.CPListElement;
import descent.internal.ui.wizards.buildpaths.CPListElementAttribute;
import descent.internal.ui.wizards.buildpaths.CPListLabelProvider;
import descent.internal.ui.wizards.buildpaths.newsourcepage.DialogPackageExplorerActionGroup.DialogExplorerActionContext;
import descent.internal.ui.workingsets.WorkingSetModel;

/**
 * A package explorer widget that can be used in dialogs. It uses its own 
 * content provider, label provider, element sorter and filter to display
 * elements that are not shown usually in the package explorer of the
 * workspace.
 */
public class DialogPackageExplorer implements IMenuListener, ISelectionChangedListener {
    /**
     * A extended content provider for the package explorer which can additionally display
     * an output folder item.
     */
    private final class PackageContentProvider extends StandardJavaElementContentProvider {
        public PackageContentProvider() {
            super();
        }
        
        /**
         * Get the elements of the current project
         * 
         * @param element the element to get the children from, will
         * not be used, instead the project childrens are returned directly
         * @return returns the children of the project
         */
        public Object[] getElements(Object element) {
            if (fCurrJProject == null)
                return new Object[0];
            return new Object[] {fCurrJProject};
        }
        
        /**
         * Get the children of the current <code>element</code>. If the
         * element is of type <code>IPackageFragmentRoot</code> and
         * displaying the output folders is selected, then an icon for
         * the output folder is created and displayed additionally.
         * 
         * @param element the current element to get the children from
         * @return an array of children
         */
        public Object[] getChildren(Object element) {
            Object[] children= super.getChildren(element);
            if (((element instanceof IPackageFragmentRoot && !((IPackageFragmentRoot)element).isArchive()) || 
                    (element instanceof IJavaProject && fCurrJProject.isOnClasspath(fCurrJProject))) && isSelected()) {
                try {
                    IClasspathEntry entry;
                    if (element instanceof IPackageFragmentRoot)
                        entry= ((IPackageFragmentRoot) element).getRawClasspathEntry();
                    else
                        entry= ClasspathModifier.getClasspathEntryFor(fCurrJProject.getPath(), fCurrJProject, IClasspathEntry.CPE_SOURCE);
                    CPListElement parent= CPListElement.createFromExisting(entry, fCurrJProject);                    
                    CPListElementAttribute outputFolder= new CPListElementAttribute(parent, CPListElement.OUTPUT, 
                            parent.getAttribute(CPListElement.OUTPUT), true);
                    Object[] extendedChildren= new Object[children.length + 1];
                    System.arraycopy(children, 0, extendedChildren, 1, children.length);
                    extendedChildren[0]= outputFolder;
                    return extendedChildren;
                } catch (JavaModelException e) {
                    JavaPlugin.log(e);
                }
                return null;
            }
            else
                return children;                    
        }
    }
    
    /**
     * A extended label provider for the package explorer which can additionally display
     * an output folder item.
     */
    private final class PackageLabelProvider extends AppearanceAwareLabelProvider {
        private CPListLabelProvider outputFolderLabel;
        
        public PackageLabelProvider(long textFlags, int imageFlags) {
            super(textFlags, imageFlags);
            outputFolderLabel= new CPListLabelProvider();
        }
        
        public String getText(Object element) {
            if (element instanceof CPListElementAttribute)
                return outputFolderLabel.getText(element);
            String text= super.getText(element);
            try {
                if (element instanceof IPackageFragmentRoot) {
                    IPackageFragmentRoot root= (IPackageFragmentRoot)element;
                    if (root.exists() && ClasspathModifier.filtersSet(root)) {
                        IClasspathEntry entry= root.getRawClasspathEntry();
                        int excluded= entry.getExclusionPatterns().length;
                        if (excluded == 1)
                            return Messages.format(NewWizardMessages.DialogPackageExplorer_LabelProvider_SingleExcluded, text); 
                        else if (excluded > 1)
                            return Messages.format(NewWizardMessages.DialogPackageExplorer_LabelProvider_MultiExcluded, new Object[] {text, new Integer(excluded)}); 
                    }
                }
                if (element instanceof IJavaProject) {
                    IJavaProject project= (IJavaProject)element;
                    if (project.exists() && project.isOnClasspath(project)) {
                        IPackageFragmentRoot root= project.findPackageFragmentRoot(project.getPath());
                        if (ClasspathModifier.filtersSet(root)) {
                            IClasspathEntry entry= root.getRawClasspathEntry();
                            int excluded= entry.getExclusionPatterns().length;
                            if (excluded == 1)
                                return Messages.format(NewWizardMessages.DialogPackageExplorer_LabelProvider_SingleExcluded, text); 
                            else if (excluded > 1)
                                return Messages.format(NewWizardMessages.DialogPackageExplorer_LabelProvider_MultiExcluded, new Object[] {text, new Integer(excluded)}); 
                        }
                    }
                }
                if (element instanceof IFile || element instanceof IFolder) {
                    IResource resource= (IResource)element;
                        if (resource.exists() && ClasspathModifier.isExcluded(resource, fCurrJProject))
                            return Messages.format(NewWizardMessages.DialogPackageExplorer_LabelProvider_Excluded, text); 
                }
            } catch (JavaModelException e) {
                JavaPlugin.log(e);
            }
            return text;
        }
        
        /* (non-Javadoc)
         * @see descent.internal.ui.viewsupport.JavaUILabelProvider#getForeground(java.lang.Object)
         */
        public Color getForeground(Object element) {
            try {
                if (element instanceof IPackageFragmentRoot) {
                    IPackageFragmentRoot root= (IPackageFragmentRoot)element;
                    if (root.exists() && ClasspathModifier.filtersSet(root))
                        return getBlueColor();
                }
                if (element instanceof IJavaProject) {
                    IJavaProject project= (IJavaProject)element;
                    if (project.exists() && project.isOnClasspath(project)) {
                        IPackageFragmentRoot root= project.findPackageFragmentRoot(project.getPath());
                        if (root != null && ClasspathModifier.filtersSet(root))
                            return getBlueColor();
                    }
                }
                if (element instanceof IFile || element instanceof IFolder) {
                    IResource resource= (IResource)element;
                    if (resource.exists() && ClasspathModifier.isExcluded(resource, fCurrJProject))
                        return getBlueColor();
                } 
            } catch (JavaModelException e) {
                JavaPlugin.log(e);
            }
            return null;
        }
        
        private Color getBlueColor() {
            return Display.getCurrent().getSystemColor(SWT.COLOR_BLUE);
        }
        
        public Image getImage(Object element) {
            if (element instanceof CPListElementAttribute)
                return outputFolderLabel.getImage(element);
            return super.getImage(element);
        }
        
        public void dispose() {
            outputFolderLabel.dispose();
            super.dispose();
        }
    }
    
    /**
     * A extended element sorter for the package explorer which displays the output
     * folder (if any) as first child of a source folder. The other java elements
     * are sorted in the normal way.
     */
    private final class ExtendedJavaElementSorter extends JavaElementSorter {
        public ExtendedJavaElementSorter() {
            super();
        }
        
        public int compare(Viewer viewer, Object e1, Object e2) {
            if (e1 instanceof CPListElementAttribute)
                return -1;
            if (e2 instanceof CPListElementAttribute)
                return 1;
            return super.compare(viewer, e1, e2);
        }
    }
    
    /**
     * A extended filter for the package explorer which filters libraries and
     * files if their name is either ".classpath" or ".project".
     */
    private final class PackageFilter extends LibraryFilter {
        private OutputFolderFilter fOutputFolderFilter= new OutputFolderFilter();
        public boolean select(Viewer viewer, Object parentElement, Object element) {
            try {
                if (element instanceof IFile) {
                    IFile file= (IFile) element;
                    if (file.getName().equals(".classpath") || file.getName().equals(".project")) //$NON-NLS-1$//$NON-NLS-2$
                        return false;
                }
                if (element instanceof IPackageFragmentRoot) {
                    IClasspathEntry cpe= ((IPackageFragmentRoot)element).getRawClasspathEntry();
                    if (cpe == null || cpe.getEntryKind() == IClasspathEntry.CPE_CONTAINER)
                        return false;
                }
            } catch (JavaModelException e) {
                JavaPlugin.log(e);
            }
            /*if (element instanceof IPackageFragmentRoot) {
                IPackageFragmentRoot root= (IPackageFragmentRoot)element;
                if (root.getElementName().endsWith(".jar") || root.getElementName().endsWith(".zip")) //$NON-NLS-1$ //$NON-NLS-2$
                    return false;
            }*/
            return /*super.select(viewer, parentElement, element) &&*/ fOutputFolderFilter.select(viewer, parentElement, element);
        }
    }
    
    /** The tree showing the project like in the package explorer */
    private TreeViewer fPackageViewer;
    /** The tree's context menu */
    private Menu fContextMenu;
    /** The action group which is used to fill the context menu. The action group
     * is also called if the selection on the tree changes */
    private DialogPackageExplorerActionGroup fActionGroup;
    /**
     * Flag to indicate whether output folders
     * can be created or not. This is used to
     * set the content correctly in the case
     * that a IPackageFragmentRoot is selected.
     * 
     * @see #showOutputFolders(boolean)
     * @see #isSelected()
     */
    private boolean fShowOutputFolders= false;
    
    /** Stores the current selection in the tree 
     * @see #getSelection()
     */
    private IStructuredSelection fCurrentSelection;
    
    /** The current java project
     * @see #setInput(IJavaProject)
     */
    private IJavaProject fCurrJProject;
    
    public DialogPackageExplorer() {
        fActionGroup= null;
        fCurrJProject= null;
        fCurrentSelection= new StructuredSelection();
    }
    
    public Control createControl(Composite parent) {
        fPackageViewer= new TreeViewer(parent, SWT.MULTI);
        fPackageViewer.setComparer(WorkingSetModel.COMPARER);
        fPackageViewer.addFilter(new PackageFilter());
        fPackageViewer.setSorter(new ExtendedJavaElementSorter());
        fPackageViewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(DoubleClickEvent event) {
                Object element= ((IStructuredSelection)event.getSelection()).getFirstElement();
                if (fPackageViewer.isExpandable(element)) {
                    fPackageViewer.setExpandedState(element, !fPackageViewer.getExpandedState(element));
                } else {
                	if (element instanceof CPListElementAttribute) {
						CPListElementAttribute attribute= (CPListElementAttribute)element;
                		if (attribute.getKey().equals(CPListElement.OUTPUT)) {
                			ClasspathModifierAction[] actions= fActionGroup.getActions();
                			for (int i= 0; i < actions.length; i++) {
								if (actions[i].getOperation().getTypeId() == IClasspathInformationProvider.EDIT_OUTPUT) {
									actions[i].run();
									return;
								}
							}
                		}
                	}
                }
            }
        });
        fPackageViewer.addSelectionChangedListener(this);
        
        MenuManager menuMgr= new MenuManager("#PopupMenu"); //$NON-NLS-1$
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(this);
        fContextMenu= menuMgr.createContextMenu(fPackageViewer.getTree());
        fPackageViewer.getTree().setMenu(fContextMenu);
        parent.addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent e) {
                fContextMenu.dispose();
            }
        });
        
        return fPackageViewer.getControl();
    }
    
    /**
     * Sets the action group for the package explorer.
     * The action group is necessary to populate the 
     * context menu with available actions. If no 
     * context menu is needed, then this method does not 
     * have to be called.
     * 
     * Should only be called once.
     *  
     * @param actionGroup the action group to be used for 
     * the context menu.
     */
    public void setActionGroup(final DialogPackageExplorerActionGroup actionGroup) {
        fActionGroup= actionGroup;
        fPackageViewer.getControl().addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent e) {
                if (actionGroup != null)
                    actionGroup.dispose();
            }
        });
    }
    
    /**
     * Populate the context menu with the necessary actions.
     * 
     * @see org.eclipse.jface.action.IMenuListener#menuAboutToShow(org.eclipse.jface.action.IMenuManager)
     */
    public void menuAboutToShow(IMenuManager manager) {
        if (fActionGroup == null) // no context menu
            return;
        JavaPlugin.createStandardGroups(manager);
        fActionGroup.fillContextMenu(manager);
    }
    
    /**
     * Set the content and label provider of the
     * <code>fPackageViewer</code>
     */
    public void setContentProvider() {
		PackageContentProvider contentProvider= new PackageContentProvider();
		PackageLabelProvider labelProvider= new PackageLabelProvider(AppearanceAwareLabelProvider.DEFAULT_TEXTFLAGS | JavaElementLabels.P_COMPRESSED,
				AppearanceAwareLabelProvider.DEFAULT_IMAGEFLAGS | JavaElementImageProvider.SMALL_ICONS);
		fPackageViewer.setContentProvider(contentProvider);
		fPackageViewer.setLabelProvider(new DecoratingJavaLabelProvider(labelProvider, false));
    }
    
    /**
     * Set the input for the package explorer.
     * 
     * @param project the project to be displayed
     */
    public void setInput(IJavaProject project) {
        fCurrJProject= project;
        fPackageViewer.setInput(new Object());
        IStructuredSelection selection= new StructuredSelection(project);
        fPackageViewer.setSelection(selection);
        fPackageViewer.expandToLevel(2);
        fCurrentSelection= selection;
        try {
            if (fActionGroup != null)
                fActionGroup.refresh(new DialogExplorerActionContext(fCurrentSelection, fCurrJProject));
        } catch (JavaModelException e) {
            JavaPlugin.log(e);
        }
    }
    
    /**
     * Refresh the project tree
     */
    public void refresh() {
        fPackageViewer.refresh(true);
    }
    
    /**
     * Set the selection and focus to the list of elements
     * @param elements the object to be selected and displayed
     */
    public void setSelection(List elements) {
        if (elements == null || elements.size() == 0)
            return;
        fPackageViewer.refresh();
        IStructuredSelection selection= new StructuredSelection(elements);
        fPackageViewer.setSelection(selection, true);
        fPackageViewer.getTree().setFocus();
        
        if (elements.size() == 1 && elements.get(0) instanceof IJavaProject)
            fPackageViewer.expandToLevel(elements.get(0), 1);
    }
    
    /**
     * The current list of selected elements. The 
     * list may be empty if no element is selected.
     * 
     * @return the current selection
     */
    public IStructuredSelection getSelection() {
        return fCurrentSelection;
    }
    
    /**
     * Get the viewer's control
     * 
     * @return the viewers control
     */
    public Control getViewerControl() {
        return fPackageViewer.getControl();
    }
    
    /**
     * Flag to indicate whether output folders
     * can be created or not. This is used to
     * set the content correctly in the case
     * that a IPackageFragmentRoot is selected.
     */
    private boolean isSelected() {
        return fShowOutputFolders;
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
        fShowOutputFolders= showOutputFolders;
        fActionGroup.showOutputFolders(showOutputFolders);
        fPackageViewer.refresh();
    }

    /**
     * Inform the <code>fActionGroup</code> about the selection change and store the 
     * latest selection.
     * 
     * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
     * @see DialogPackageExplorerActionGroup#setContext(DialogExplorerActionContext)
     */
    public void selectionChanged(SelectionChangedEvent event) {
        fCurrentSelection= (IStructuredSelection)event.getSelection();
        try {
            if (fActionGroup != null)
                fActionGroup.setContext(new DialogExplorerActionContext(fCurrentSelection, fCurrJProject));
        } catch (JavaModelException e) {
            JavaPlugin.log(e);
        }
    }
}
