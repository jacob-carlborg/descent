package descent.internal.core.hierarchy;

import java.util.ArrayList;

import org.eclipse.core.runtime.CoreException;
import descent.core.*;
import descent.core.search.IJavaSearchScope;
import descent.internal.core.CompilationUnit;
import descent.internal.core.JavaElement;
import descent.internal.core.Openable;
import descent.internal.core.Region;
import descent.internal.core.TypeVector;

public class RegionBasedTypeHierarchy extends TypeHierarchy {
	/**
	 * The region of types for which to build the hierarchy
	 */
	protected IRegion region;
	
/**
 * Creates a TypeHierarchy on the types in the specified region,
 * considering first the given working copies,
 * using the projects in the given region for a name lookup context. If a specific
 * type is also specified, the type hierarchy is pruned to only
 * contain the branch including the specified type.
 */
public RegionBasedTypeHierarchy(IRegion region, ICompilationUnit[] workingCopies, IType type, boolean computeSubtypes) {
	super(type, workingCopies, (IJavaSearchScope)null, computeSubtypes);
	
	Region newRegion = new Region() {
		public void add(IJavaElement element) {
			if (!contains(element)) {
				//"new" element added to region
				removeAllChildren(element);
				fRootElements.add(element);
				if (element.getElementType() == IJavaElement.JAVA_PROJECT) {
					// add jar roots as well so that jars don't rely on their parent to know 
					// if they are contained in the region
					// (see https://bugs.eclipse.org/bugs/show_bug.cgi?id=146615)
					try {
						IPackageFragmentRoot[] roots = ((IJavaProject) element).getPackageFragmentRoots();
						for (int i = 0, length = roots.length; i < length; i++) {
							if (roots[i].isArchive() && !fRootElements.contains(roots[i]))
								fRootElements.add(roots[i]);
						}
					} catch (JavaModelException e) {
						// project doesn't exist
					}
				}
				fRootElements.trimToSize();
			}
		}
	};
	IJavaElement[] elements = region.getElements();
	for (int i = 0, length = elements.length; i < length; i++) {
		newRegion.add(elements[i]);
		
	}
	this.region = newRegion;
	if (elements.length > 0)
		this.project = elements[0].getJavaProject();
}
/*
 * @see TypeHierarchy#initializeRegions
 */
protected void initializeRegions() {
	super.initializeRegions();
	IJavaElement[] roots = this.region.getElements();
	for (int i = 0; i < roots.length; i++) {
		IJavaElement root = roots[i];
		if (root instanceof IOpenable) {
			this.files.put(root, new ArrayList());
		} else {
			Openable o = (Openable) ((JavaElement) root).getOpenableParent();
			if (o != null) {
				this.files.put(o, new ArrayList());
			}
		}
		checkCanceled();
	}
}
/**
 * Compute this type hierarchy.
 */
protected void compute() throws JavaModelException, CoreException {
	HierarchyBuilder builder = new RegionBasedHierarchyBuilder(this);
	builder.build(this.computeSubtypes);
}
protected boolean isAffectedByOpenable(IJavaElementDelta delta, IJavaElement element) {
	// change to working copy
	if (element instanceof CompilationUnit && ((CompilationUnit)element).isWorkingCopy()) {
		return super.isAffectedByOpenable(delta, element);
	}

	// if no focus, hierarchy is affected if the element is part of the region
	if (this.focusType == null) {
		return this.region.contains(element);
	} else {
		return super.isAffectedByOpenable(delta, element);
	}
}
/**
 * Returns the java project this hierarchy was created in.
 */
public IJavaProject javaProject() {
	return this.project;
}
public void pruneDeadBranches() {
	pruneDeadBranches(getRootClasses());
	pruneDeadBranches(getRootInterfaces());
}
/*
 * Returns whether all subtypes of the given type have been pruned.
 */
private boolean pruneDeadBranches(IType type) {
	TypeVector subtypes = (TypeVector)this.typeToSubtypes.get(type);
	if (subtypes == null) return true;
	pruneDeadBranches(subtypes.copy().elements());
	subtypes = (TypeVector)this.typeToSubtypes.get(type);
	return (subtypes == null || subtypes.size == 0);
}
private void pruneDeadBranches(IType[] types) {
	for (int i = 0, length = types.length; i < length; i++) {
		IType type = types[i];
		if (pruneDeadBranches(type) && !this.region.contains(type)) {
			removeType(type);
		}
	}
}
/**
 * Removes all the subtypes of the given type from the type hierarchy,
 * removes its superclass entry and removes the references from its super types.
 */
protected void removeType(IType type) {
	IType[] subtypes = this.getSubtypes(type);
	this.typeToSubtypes.remove(type);
	if (subtypes != null) {
		for (int i= 0; i < subtypes.length; i++) {
			this.removeType(subtypes[i]);
		}
	}
	IType superclass = (IType)this.classToSuperclass.remove(type);
	if (superclass != null) {
		TypeVector types = (TypeVector)this.typeToSubtypes.get(superclass);
		if (types != null) types.remove(type);
	}
	IType[] superinterfaces = (IType[])this.typeToSuperInterfaces.remove(type);
	if (superinterfaces != null) {
		for (int i = 0, length = superinterfaces.length; i < length; i++) {
			IType superinterface = superinterfaces[i];
			TypeVector types = (TypeVector)this.typeToSubtypes.get(superinterface);
			if (types != null) types.remove(type);
		}
	}
	this.interfaces.remove(type);
}

}
