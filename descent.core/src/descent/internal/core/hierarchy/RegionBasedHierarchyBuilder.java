package descent.internal.core.hierarchy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import descent.core.IClassFile;
import descent.core.ICompilationUnit;
import descent.core.IJavaElement;
import descent.core.IJavaProject;
import descent.core.IPackageFragment;
import descent.core.IPackageFragmentRoot;
import descent.core.IType;
import descent.core.JavaModelException;
import descent.internal.core.JavaModelManager;
import descent.internal.core.JavaProject;
import descent.internal.core.Openable;
import descent.internal.core.SearchableEnvironment;

public class RegionBasedHierarchyBuilder extends HierarchyBuilder {
	
	public RegionBasedHierarchyBuilder(TypeHierarchy hierarchy)
		throws JavaModelException {
			
		super(hierarchy);
	}
	
public void build(boolean computeSubtypes) {
		
	JavaModelManager manager = JavaModelManager.getJavaModelManager();
	try {
		// optimize access to zip files while building hierarchy
		manager.cacheZipFiles();
				
		if (this.hierarchy.focusType == null || computeSubtypes) {
			IProgressMonitor typeInRegionMonitor = 
				this.hierarchy.progressMonitor == null ? 
					null : 
					new SubProgressMonitor(this.hierarchy.progressMonitor, 30);
			HashMap allOpenablesInRegion = determineOpenablesInRegion(typeInRegionMonitor);
			this.hierarchy.initialize(allOpenablesInRegion.size());
			IProgressMonitor buildMonitor = 
				this.hierarchy.progressMonitor == null ? 
					null : 
					new SubProgressMonitor(this.hierarchy.progressMonitor, 70);
			createTypeHierarchyBasedOnRegion(allOpenablesInRegion, buildMonitor);
			((RegionBasedTypeHierarchy)this.hierarchy).pruneDeadBranches();
		} else {
			this.hierarchy.initialize(1);
			this.buildSupertypes();
		}
	} finally {
		manager.flushZipFiles();
	}
}
/**
 * Configure this type hierarchy that is based on a region.
 */
private void createTypeHierarchyBasedOnRegion(HashMap allOpenablesInRegion, IProgressMonitor monitor) {
	
	int size = allOpenablesInRegion.size();
	if (size == 0) {
		if (monitor != null) monitor.done();
		return;
	}
		
	this.infoToHandle = new HashMap(size);
	Iterator javaProjects = allOpenablesInRegion.keySet().iterator();
	while (javaProjects.hasNext()) {
		JavaProject project = (JavaProject) javaProjects.next();
		ArrayList allOpenables = (ArrayList) allOpenablesInRegion.get(project);
		Openable[] openables = new Openable[allOpenables.size()];
		allOpenables.toArray(openables);
	
		try {
			// resolve
			if (monitor != null) monitor.beginTask("", size * 2/* 1 for build binding, 1 for connect hierarchy*/); //$NON-NLS-1$
			SearchableEnvironment searchableEnvironment = project.newSearchableNameEnvironment(this.hierarchy.workingCopies);
			this.nameLookup = searchableEnvironment.nameLookup;
			this.hierarchyResolver.resolve(openables, null, monitor);
		} catch (JavaModelException e) {
			// project doesn't exit: ignore
		} finally {
			if (monitor != null) monitor.done();
		}
	}
}
	
	/**
	 * Returns all of the openables defined in the region of this type hierarchy.
	 * Returns a map from IJavaProject to ArrayList of Openable
	 */
	private HashMap determineOpenablesInRegion(IProgressMonitor monitor) {

		try {
			HashMap allOpenables = new HashMap();
			IJavaElement[] roots =
				((RegionBasedTypeHierarchy) this.hierarchy).region.getElements();
			int length = roots.length;
			if (monitor != null) monitor.beginTask("", length); //$NON-NLS-1$
			for (int i = 0; i <length; i++) {
				IJavaElement root = roots[i];
				IJavaProject javaProject = root.getJavaProject();
				ArrayList openables = (ArrayList) allOpenables.get(javaProject);
				if (openables == null) {
					openables = new ArrayList();
					allOpenables.put(javaProject, openables);
				}
				switch (root.getElementType()) {
					case IJavaElement.JAVA_PROJECT :
						injectAllOpenablesForJavaProject((IJavaProject) root, openables);
						break;
					case IJavaElement.PACKAGE_FRAGMENT_ROOT :
						injectAllOpenablesForPackageFragmentRoot((IPackageFragmentRoot) root, openables);
						break;
					case IJavaElement.PACKAGE_FRAGMENT :
						injectAllOpenablesForPackageFragment((IPackageFragment) root, openables);
						break;
					case IJavaElement.CLASS_FILE :
					case IJavaElement.COMPILATION_UNIT :
						openables.add(root);
						break;
					case IJavaElement.TYPE :
						IType type = (IType)root;
						if (type.isBinary()) {
							openables.add(type.getClassFile());
						} else {
							openables.add(type.getCompilationUnit());
						}
						break;
					default :
						break;
				}
				worked(monitor, 1);
			}
			return allOpenables;
		} finally {
			if (monitor != null) monitor.done();
		}
	}
	
	/**
	 * Adds all of the openables defined within this java project to the
	 * list.
	 */
	private void injectAllOpenablesForJavaProject(
		IJavaProject project,
		ArrayList openables) {
		try {
			IPackageFragmentRoot[] devPathRoots =
				((JavaProject) project).getPackageFragmentRoots();
			if (devPathRoots == null) {
				return;
			}
			for (int j = 0; j < devPathRoots.length; j++) {
				IPackageFragmentRoot root = devPathRoots[j];
				injectAllOpenablesForPackageFragmentRoot(root, openables);
			}
		} catch (JavaModelException e) {
			// ignore
		}
	}
	
	/**
	 * Adds all of the openables defined within this package fragment to the
	 * list.
	 */
	private void injectAllOpenablesForPackageFragment(
		IPackageFragment packFrag,
		ArrayList openables) {
			
		try {
			IPackageFragmentRoot root = (IPackageFragmentRoot) packFrag.getParent();
			int kind = root.getKind();
			if (kind != 0) {
				boolean isSourcePackageFragment = (kind == IPackageFragmentRoot.K_SOURCE);
				if (isSourcePackageFragment) {
					ICompilationUnit[] cus = packFrag.getCompilationUnits();
					for (int i = 0, length = cus.length; i < length; i++) {
						openables.add(cus[i]);
					}
				} else {
					IClassFile[] classFiles = packFrag.getClassFiles();
					for (int i = 0, length = classFiles.length; i < length; i++) {
						openables.add(classFiles[i]);
					}
				}
			}
		} catch (JavaModelException e) {
			// ignore
		}
	}
	
	/**
	 * Adds all of the openables defined within this package fragment root to the
	 * list.
	 */
	private void injectAllOpenablesForPackageFragmentRoot(
		IPackageFragmentRoot root,
		ArrayList openables) {
		try {
			IJavaElement[] packFrags = root.getChildren();
			for (int k = 0; k < packFrags.length; k++) {
				IPackageFragment packFrag = (IPackageFragment) packFrags[k];
				injectAllOpenablesForPackageFragment(packFrag, openables);
			}
		} catch (JavaModelException e) {
			return;
		}
	}
	
}
