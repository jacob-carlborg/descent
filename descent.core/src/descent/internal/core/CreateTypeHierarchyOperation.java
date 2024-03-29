package descent.internal.core;

import descent.core.*;
import descent.core.IJavaElement;
import descent.core.IJavaModelStatus;
import descent.core.IJavaModelStatusConstants;
import descent.core.IJavaProject;
import descent.core.IRegion;
import descent.core.IType;
import descent.core.ITypeHierarchy;
import descent.core.JavaModelException;
import descent.core.search.IJavaSearchScope;
import descent.internal.core.hierarchy.RegionBasedTypeHierarchy;
import descent.internal.core.hierarchy.TypeHierarchy;

/**
 * This operation creates an <code>ITypeHierarchy</code> for a specific type within
 * a specified region, or for all types within a region. The specified
 * region limits the number of resolved subtypes (to the subset of
 * types in the specified region). The resolved supertypes may go outside
 * of the specified region in order to reach the root(s) of the type
 * hierarchy. A Java Project is required to provide a context (classpath)
 * to use while resolving supertypes and subtypes.
 *
 * @see ITypeHierarchy
 */

public class CreateTypeHierarchyOperation extends JavaModelOperation {
	/**
	 * The generated type hierarchy
	 */
	protected TypeHierarchy typeHierarchy;
	
/**
 * Constructs an operation to create a type hierarchy for the
 * given type within the specified region, in the context of
 * the given project.
 */
public CreateTypeHierarchyOperation(IRegion region, ICompilationUnit[] workingCopies, IType element, boolean computeSubtypes) {
	super(element);
	this.typeHierarchy = new RegionBasedTypeHierarchy(region, workingCopies, element, computeSubtypes);
}
/**
 * Constructs an operation to create a type hierarchy for the
 * given type and working copies.
 */
public CreateTypeHierarchyOperation(IType element, ICompilationUnit[] workingCopies, IJavaSearchScope scope, boolean computeSubtypes) {
	super(element);
	ICompilationUnit[] copies;
	if (workingCopies != null) {
		int length = workingCopies.length;
		copies = new ICompilationUnit[length];
		System.arraycopy(workingCopies, 0, copies, 0, length);
	} else {
		copies = null;
	}
	this.typeHierarchy = new TypeHierarchy(element, copies, scope, computeSubtypes);
}
/**
 * Constructs an operation to create a type hierarchy for the
 * given type and working copies.
 */
public CreateTypeHierarchyOperation(IType element, ICompilationUnit[] workingCopies, IJavaProject project, boolean computeSubtypes) {
	super(element);
	ICompilationUnit[] copies;
	if (workingCopies != null) {
		int length = workingCopies.length;
		copies = new ICompilationUnit[length];
		System.arraycopy(workingCopies, 0, copies, 0, length);
	} else {
		copies = null;
	}
	this.typeHierarchy = new TypeHierarchy(element, copies, project, computeSubtypes);
}
/**
 * Performs the operation - creates the type hierarchy
 * @exception JavaModelException The operation has failed.
 */
protected void executeOperation() throws JavaModelException {
	this.typeHierarchy.refresh(this);
}
/**
 * Returns the generated type hierarchy.
 */
public ITypeHierarchy getResult() {
	return this.typeHierarchy;
}
/**
 * @see JavaModelOperation
 */
public boolean isReadOnly() {
	return true;
}
/**
 * Possible failures: <ul>
 *	<li>NO_ELEMENTS_TO_PROCESS - at least one of a type or region must
 *			be provided to generate a type hierarchy.
 *	<li>ELEMENT_NOT_PRESENT - the provided type or type's project does not exist
 * </ul>
 */
public IJavaModelStatus verify() {
	IJavaElement elementToProcess= getElementToProcess();
	if (elementToProcess == null && !(this.typeHierarchy instanceof RegionBasedTypeHierarchy)) {
		return new JavaModelStatus(IJavaModelStatusConstants.NO_ELEMENTS_TO_PROCESS);
	}
	if (elementToProcess != null && !elementToProcess.exists()) {
		return new JavaModelStatus(IJavaModelStatusConstants.ELEMENT_DOES_NOT_EXIST, elementToProcess);
	}
	IJavaProject project = this.typeHierarchy.javaProject();
	if (project != null && !project.exists()) {
		return new JavaModelStatus(IJavaModelStatusConstants.ELEMENT_DOES_NOT_EXIST, project);
	}
	return JavaModelStatus.VERIFIED_OK;
}
}
