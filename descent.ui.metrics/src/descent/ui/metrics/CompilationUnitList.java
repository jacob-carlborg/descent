package descent.ui.metrics;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

import descent.core.IClasspathEntry;
import descent.core.ICompilationUnit;
import descent.core.IJavaElement;
import descent.core.IJavaProject;
import descent.core.IPackageFragment;
import descent.core.IPackageFragmentRoot;
import descent.core.JavaCore;
import descent.core.JavaModelException;
import descent.ui.metrics.properties.ExcludedResources;

final class CompilationUnitList {
    private static final String JAVA_SUFFIX = ".d";

    private List compilationUnits;
    private ExcludedResources excludedResources;
    private IJavaProject project;

    public static CompilationUnitList createFullList(IProject project) throws CoreException, IOException {
        CompilationUnitList list = new CompilationUnitList(project);
        list.buildComplete();

        return list;
    }

    public static CompilationUnitList createResourceDeltaList(IProject project, IResourceDelta delta) throws CoreException, IOException {
        CompilationUnitList list = new CompilationUnitList(project);
        list.addTypesForResourceDelta(delta);

        return list;
    }

    private CompilationUnitList(IProject project) throws IOException {
        this.project = JavaCore.create(project);
        excludedResources = new ExcludedResources(project);
        compilationUnits = new ArrayList();
    }

    private void buildComplete() throws JavaModelException {
        IPackageFragmentRoot[] packageFragmentRoots = project.getAllPackageFragmentRoots();

        for (int i = 0; i < packageFragmentRoots.length; i++) {
            addTypesForPackageFragmentRoot(packageFragmentRoots[i]);
        }
    }

    private void addTypesForPackageFragmentRoot(IPackageFragmentRoot packageFragmentRoot) throws JavaModelException {
        if (packageFragmentRoot.getKind() == IPackageFragmentRoot.K_SOURCE && packageFragmentRoot.getCorrespondingResource() != null && project.getProject().equals(packageFragmentRoot.getCorrespondingResource().getProject())) {
            addTypesForJavaElements(packageFragmentRoot.getChildren());
        }
    }

    public int size() {
        return compilationUnits.size();
    }

    public ICompilationUnit get(int index) {
        return (ICompilationUnit) compilationUnits.get(index);
    }

    protected void addCompilationUnit(ICompilationUnit compilationUnit) throws JavaModelException {
        if (compilationUnit.isStructureKnown() && compilationUnit.isConsistent() && !excludedResources.contains((IFile) compilationUnit.getCorrespondingResource())) {
            compilationUnits.add(compilationUnit);
        }
    }

    private void addTypesForJavaElements(IJavaElement[] elements) throws JavaModelException {
        for (int i = 0; i < elements.length; i++) {
            addTypesForJavaElement(elements[i]);
        }
    }

    protected void addTypesForJavaElement(IJavaElement element) throws JavaModelException {
        if (project.equals(element.getJavaProject())) {
            switch (element.getElementType()) {
                case IJavaElement.PACKAGE_FRAGMENT : {
                    addTypesForJavaElements(((IPackageFragment) element).getChildren());
                    break;
                }
                case IJavaElement.COMPILATION_UNIT : {
                    addCompilationUnit((ICompilationUnit) element);
                    break;
                }
            }
        }
    }

    private void addTypesForResourceDelta(IResourceDelta delta) throws JavaModelException {
        IResourceDelta[] affectedChildren = delta.getAffectedChildren();
        for (int i = 0; i < affectedChildren.length; i++) {
            IResourceDelta affectedChild = affectedChildren[i];
            addTypesForAffectedResourceDelta(affectedChild);
        }
    }

    private void addTypesForAffectedResourceDelta(IResourceDelta affectedChild) throws JavaModelException {
        IResource affectedResource = affectedChild.getResource();
        if (isJavaFile(affectedResource) && isAddedOrChanged(affectedChild) && isOnClasspath(affectedResource)) {
            addTypesForJavaElement(JavaCore.create(affectedResource));
        } else if (isFolder(affectedResource)) {
            addTypesForResourceDelta(affectedChild);
        }
    }
    
    private boolean isOnClasspath(IResource resource) throws JavaModelException {
        IPath resourcePath = resource.getFullPath();
        IClasspathEntry[] classpath = project.getResolvedClasspath(true);
        for (int i = 0; i < classpath.length; i++) {
            if (classpath[i].getPath().makeRelative().isPrefixOf(resourcePath)) {
                return true;
            }
        }
        
        return false;
    }

    private boolean isJavaFile(IResource resource) {
        return resource.getType() == IResource.FILE && resource.getName().endsWith(CompilationUnitList.JAVA_SUFFIX);
    }

    private boolean isFolder(IResource resource) {
        return resource.getType() == IResource.FOLDER;
    }

    private boolean isAddedOrChanged(IResourceDelta delta) {
        return delta.getKind() == IResourceDelta.ADDED || delta.getKind() == IResourceDelta.CHANGED;
    }
}
