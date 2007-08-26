package descent.internal.ui.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;

import org.eclipse.swt.widgets.Shell;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.IWorkingSetSelectionDialog;

import descent.core.IClasspathContainer;
import descent.core.IClasspathEntry;
import descent.core.IJavaElement;
import descent.core.IJavaProject;
import descent.core.IPackageFragment;
import descent.core.IPackageFragmentRoot;
import descent.core.IType;
import descent.core.JavaCore;
import descent.core.JavaModelException;
import descent.core.search.IJavaSearchScope;
import descent.core.search.SearchEngine;

import descent.internal.corext.util.Messages;

import descent.ui.JavaUI;

import descent.internal.ui.JavaPlugin;
import descent.internal.ui.browsing.LogicalPackage;

public class JavaSearchScopeFactory {

	private static JavaSearchScopeFactory fgInstance;
	private final IJavaSearchScope EMPTY_SCOPE= SearchEngine.createJavaSearchScope(new IJavaElement[] {});
	
	private JavaSearchScopeFactory() {
	}

	public static JavaSearchScopeFactory getInstance() {
		if (fgInstance == null)
			fgInstance= new JavaSearchScopeFactory();
		return fgInstance;
	}

	public IWorkingSet[] queryWorkingSets() throws JavaModelException {
		Shell shell= JavaPlugin.getActiveWorkbenchShell();
		if (shell == null)
			return null;
		IWorkingSetSelectionDialog dialog= PlatformUI.getWorkbench().getWorkingSetManager().createWorkingSetSelectionDialog(shell, true);
		if (dialog.open() == Window.OK) {
			IWorkingSet[] workingSets= dialog.getSelection();
			if (workingSets.length > 0)
				return workingSets;
		}
		return null;
	}

	public IJavaSearchScope createJavaSearchScope(IWorkingSet[] workingSets, boolean includeJRE) {
		if (workingSets == null || workingSets.length < 1)
			return EMPTY_SCOPE;

		Set javaElements= new HashSet(workingSets.length * 10);
		for (int i= 0; i < workingSets.length; i++) {
			IWorkingSet workingSet= workingSets[i];
			if (workingSet.isEmpty() && workingSet.isAggregateWorkingSet()) {
				return createWorkspaceScope(includeJRE);
			}
			addJavaElements(javaElements, workingSet);
		}
		return createJavaSearchScope(javaElements, includeJRE);
	}
	
	public IJavaSearchScope createJavaSearchScope(IWorkingSet workingSet, boolean includeJRE) {
		Set javaElements= new HashSet(10);
		if (workingSet.isEmpty() && workingSet.isAggregateWorkingSet()) {
			return createWorkspaceScope(includeJRE);
		}
		addJavaElements(javaElements, workingSet);
		return createJavaSearchScope(javaElements, includeJRE);
	}

	public IJavaSearchScope createJavaSearchScope(IResource[] resources, boolean includeJRE) {
		if (resources == null)
			return EMPTY_SCOPE;
		Set javaElements= new HashSet(resources.length);
		addJavaElements(javaElements, resources);
		return createJavaSearchScope(javaElements, includeJRE);
	}
		
	
	public IJavaSearchScope createJavaSearchScope(ISelection selection, boolean includeJRE) {
		return createJavaSearchScope(getJavaElements(selection), includeJRE);
	}
		
	public IJavaSearchScope createJavaProjectSearchScope(String[] projectNames, boolean includeJRE) {
		ArrayList res= new ArrayList();
		IWorkspaceRoot root= ResourcesPlugin.getWorkspace().getRoot();
		for (int i= 0; i < projectNames.length; i++) {
			IJavaProject project= JavaCore.create(root.getProject(projectNames[i]));
			if (project.exists()) {
				res.add(project);
			}
		}
		return createJavaSearchScope(res, includeJRE);
	}

	public IJavaSearchScope createJavaProjectSearchScope(IJavaProject project, boolean includeJRE) {
		return SearchEngine.createJavaSearchScope(new IJavaElement[] { project }, getSearchFlags(includeJRE));
	}
	
	public IJavaSearchScope createJavaProjectSearchScope(IEditorInput editorInput, boolean includeJRE) {
		IJavaElement elem= JavaUI.getEditorInputJavaElement(editorInput);
		if (elem != null) {
			IJavaProject project= elem.getJavaProject();
			if (project != null) {
				return createJavaProjectSearchScope(project, includeJRE);
			}
		}
		return EMPTY_SCOPE;
	}
	
	public String getWorkspaceScopeDescription(boolean includeJRE) {
		return includeJRE ? SearchMessages.WorkspaceScope : SearchMessages.WorkspaceScopeNoJRE; 
	}
	
	public String getProjectScopeDescription(String[] projectNames, boolean includeJRE) {
		if (projectNames.length == 0) {
			return SearchMessages.JavaSearchScopeFactory_undefined_projects;
		}
		String scopeDescription;
		if (projectNames.length == 1) {
			String label= includeJRE ? SearchMessages.EnclosingProjectScope : SearchMessages.EnclosingProjectScopeNoJRE;
			scopeDescription= Messages.format(label, projectNames[0]);
		} else if (projectNames.length == 2) {
			String label= includeJRE ? SearchMessages.EnclosingProjectsScope2 : SearchMessages.EnclosingProjectsScope2NoJRE;
			scopeDescription= Messages.format(label, new String[] { projectNames[0], projectNames[1]});
		} else {
			String label= includeJRE ? SearchMessages.EnclosingProjectsScope : SearchMessages.EnclosingProjectsScopeNoJRE;
			scopeDescription= Messages.format(label, new String[] { projectNames[0], projectNames[1]});
		}
		return scopeDescription;
	}
	
	public String getProjectScopeDescription(IJavaProject project, boolean includeJRE) {
		if (includeJRE) {
			return Messages.format(SearchMessages.ProjectScope, project.getElementName());
		} else {
			return Messages.format(SearchMessages.ProjectScopeNoJRE, project.getElementName());
		}
	}
	
	public String getProjectScopeDescription(IEditorInput editorInput, boolean includeJRE) {
		IJavaElement elem= JavaUI.getEditorInputJavaElement(editorInput);
		if (elem != null) {
			IJavaProject project= elem.getJavaProject();
			if (project != null) {
				return getProjectScopeDescription(project, includeJRE);
			}
		}
		return Messages.format(SearchMessages.ProjectScope, "");  //$NON-NLS-1$
	}
	
	public String getHierarchyScopeDescription(IType type) {
		return Messages.format(SearchMessages.HierarchyScope, new String[] { type.getElementName() }); 
	}


	public String getSelectionScopeDescription(IJavaElement[] javaElements, boolean includeJRE) {
		if (javaElements.length == 0) {
			return SearchMessages.JavaSearchScopeFactory_undefined_selection;
		}
		String scopeDescription;
		if (javaElements.length == 1) {
			String label= includeJRE ? SearchMessages.SingleSelectionScope : SearchMessages.SingleSelectionScopeNoJRE;
			scopeDescription= Messages.format(label, javaElements[0].getElementName());
		} else if (javaElements.length == 1) {
			String label= includeJRE ? SearchMessages.DoubleSelectionScope : SearchMessages.DoubleSelectionScopeNoJRE;
			scopeDescription= Messages.format(label, new String[] { javaElements[0].getElementName(), javaElements[1].getElementName()});
		}  else {
			String label= includeJRE ? SearchMessages.SelectionScope : SearchMessages.SelectionScopeNoJRE;
			scopeDescription= Messages.format(label, new String[] { javaElements[0].getElementName(), javaElements[1].getElementName()});
		}
		return scopeDescription;
	}
	
	public String getWorkingSetScopeDescription(IWorkingSet[] workingSets, boolean includeJRE) {
		if (workingSets.length == 0) {
			return SearchMessages.JavaSearchScopeFactory_undefined_workingsets;
		}
		if (workingSets.length == 1) {
			String label= includeJRE ? SearchMessages.SingleWorkingSetScope : SearchMessages.SingleWorkingSetScopeNoJRE;
			return Messages.format(label, workingSets[0].getLabel());
		}
		Arrays.sort(workingSets, new WorkingSetComparator());
		if (workingSets.length == 2) {
			String label= includeJRE ? SearchMessages.DoubleWorkingSetScope : SearchMessages.DoubleWorkingSetScopeNoJRE;
			return Messages.format(label, new String[] { workingSets[0].getLabel(), workingSets[1].getLabel()});
		}
		String label= includeJRE ? SearchMessages.WorkingSetsScope : SearchMessages.WorkingSetsScopeNoJRE;
		return Messages.format(label, new String[] { workingSets[0].getLabel(), workingSets[1].getLabel()});
	}
	
	public IProject[] getProjects(IJavaSearchScope scope) {
		IPath[] paths= scope.enclosingProjectsAndJars();
		HashSet temp= new HashSet();
		for (int i= 0; i < paths.length; i++) {
			IResource resource= ResourcesPlugin.getWorkspace().getRoot().findMember(paths[i]);
			if (resource != null && resource.getType() == IResource.PROJECT)
				temp.add(resource);
		}
		return (IProject[]) temp.toArray(new IProject[temp.size()]);
	}

	public IJavaElement[] getJavaElements(ISelection selection) {
		if (selection instanceof IStructuredSelection && !selection.isEmpty()) {
			return getJavaElements(((IStructuredSelection)selection).toArray());
		} else {
			return new IJavaElement[0];
		}
	}

	private IJavaElement[] getJavaElements(Object[] elements) {
		if (elements.length == 0)
			return new IJavaElement[0];
		
		Set result= new HashSet(elements.length);
		for (int i= 0; i < elements.length; i++) {
			Object selectedElement= elements[i];
			if (selectedElement instanceof IJavaElement) {
				addJavaElements(result, (IJavaElement) selectedElement);
			} else if (selectedElement instanceof IResource) {
				addJavaElements(result, (IResource) selectedElement);
			} else if (selectedElement instanceof LogicalPackage) {
				addJavaElements(result, (LogicalPackage) selectedElement);
			} else if (selectedElement instanceof IWorkingSet) {
				IWorkingSet ws= (IWorkingSet)selectedElement;
				addJavaElements(result, ws);
			} else if (selectedElement instanceof IAdaptable) {
				IResource resource= (IResource) ((IAdaptable) selectedElement).getAdapter(IResource.class);
				if (resource != null)
					addJavaElements(result, resource);
			}
			
		}
		return (IJavaElement[]) result.toArray(new IJavaElement[result.size()]);
	}
	
	public IJavaSearchScope createJavaSearchScope(IJavaElement[] javaElements, boolean includeJRE) {
		if (javaElements.length == 0)
			return EMPTY_SCOPE;
		return SearchEngine.createJavaSearchScope(javaElements, getSearchFlags(includeJRE));
	}

	private IJavaSearchScope createJavaSearchScope(Collection javaElements, boolean includeJRE) {
		if (javaElements.isEmpty())
			return EMPTY_SCOPE;
		IJavaElement[] elementArray= (IJavaElement[]) javaElements.toArray(new IJavaElement[javaElements.size()]);
		return SearchEngine.createJavaSearchScope(elementArray, getSearchFlags(includeJRE));
	}
	
	private static int getSearchFlags(boolean includeJRE) {
		int flags= IJavaSearchScope.SOURCES | IJavaSearchScope.APPLICATION_LIBRARIES;
		if (includeJRE)
			flags |= IJavaSearchScope.SYSTEM_LIBRARIES;
		return flags;
	}

	private void addJavaElements(Set javaElements, IResource[] resources) {
		for (int i= 0; i < resources.length; i++)
			addJavaElements(javaElements, resources[i]);
	}

	private void addJavaElements(Set javaElements, IResource resource) {
		IJavaElement javaElement= (IJavaElement)resource.getAdapter(IJavaElement.class);
		if (javaElement == null)
			// not a Java resource
			return;
		
		if (javaElement.getElementType() == IJavaElement.PACKAGE_FRAGMENT) {
			// add other possible package fragments
			try {
				addJavaElements(javaElements, ((IFolder)resource).members());
			} catch (CoreException ex) {
				// don't add elements
			}
		}
			
		javaElements.add(javaElement);
	}

	private void addJavaElements(Set javaElements, IJavaElement javaElement) {
		javaElements.add(javaElement);
	}
	
	private void addJavaElements(Set javaElements, IWorkingSet workingSet) {
		if (workingSet == null)
			return;
		
		if (workingSet.isAggregateWorkingSet() && workingSet.isEmpty()) {
			try {
				IJavaProject[] projects= JavaCore.create(ResourcesPlugin.getWorkspace().getRoot()).getJavaProjects();
				javaElements.addAll(Arrays.asList(projects));
			} catch (JavaModelException e) {
				JavaPlugin.log(e);
			}
			return;
		}
		
		IAdaptable[] elements= workingSet.getElements();
		for (int i= 0; i < elements.length; i++) {
			IJavaElement javaElement=(IJavaElement) elements[i].getAdapter(IJavaElement.class);
			if (javaElement != null) { 
				addJavaElements(javaElements, javaElement);
				continue;
			}
			IResource resource= (IResource)elements[i].getAdapter(IResource.class);
			if (resource != null) {
				addJavaElements(javaElements, resource);
			}
			
			// else we don't know what to do with it, ignore.
		}
	}

	public void addJavaElements(Set javaElements, LogicalPackage selectedElement) {
		IPackageFragment[] packages= selectedElement.getFragments();
		for (int i= 0; i < packages.length; i++)
			addJavaElements(javaElements, packages[i]);
	}
	
	public IJavaSearchScope createWorkspaceScope(boolean includeJRE) {
		if (!includeJRE) {
			try {
				IJavaProject[] projects= JavaCore.create(ResourcesPlugin.getWorkspace().getRoot()).getJavaProjects();
				return SearchEngine.createJavaSearchScope(projects, getSearchFlags(includeJRE));
			} catch (JavaModelException e) {
				// ignore, use workspace scope instead
			}
		}
		return SearchEngine.createWorkspaceScope();
	}

	public boolean isInsideJRE(IJavaElement element) {
		IPackageFragmentRoot root= (IPackageFragmentRoot) element.getAncestor(IJavaElement.PACKAGE_FRAGMENT_ROOT);
		if (root != null) {
			try {
				IClasspathEntry entry= root.getRawClasspathEntry();
				if (entry.getEntryKind() == IClasspathEntry.CPE_CONTAINER) {
					IClasspathContainer container= JavaCore.getClasspathContainer(entry.getPath(), root.getJavaProject());
					return container != null && container.getKind() == IClasspathContainer.K_DEFAULT_SYSTEM;
				}
				return false;
			} catch (JavaModelException e) {
				JavaPlugin.log(e);
			}
		}
		return true; // include JRE in doubt
	}
}
