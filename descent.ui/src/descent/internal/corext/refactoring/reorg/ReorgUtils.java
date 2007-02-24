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
package descent.internal.corext.refactoring.reorg;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.Assert;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;

import org.eclipse.ui.IWorkingSet;

import descent.core.IClassFile;
import descent.core.ICompilationUnit;
import descent.core.IJavaElement;
import descent.core.IJavaProject;
import descent.core.IMember;
import descent.core.IMethod;
import descent.core.IPackageFragment;
import descent.core.IPackageFragmentRoot;
import descent.core.ISourceRange;
import descent.core.IType;
import descent.core.JavaCore;
import descent.core.JavaModelException;
import descent.core.Signature;

import descent.internal.corext.SourceRange;
import descent.internal.corext.refactoring.RefactoringCoreMessages;
import descent.internal.corext.refactoring.util.JavaElementUtil;
import descent.internal.corext.refactoring.util.ResourceUtil;
import descent.internal.corext.util.JavaModelUtil;
import descent.internal.corext.util.Messages;


public class ReorgUtils {

	//workaround for bug 18311
	private static final ISourceRange fgUnknownRange= new SourceRange(-1, 0);

	private ReorgUtils() {
	}

	public static boolean isArchiveMember(IJavaElement[] elements) {
		for (int i= 0; i < elements.length; i++) {
			IJavaElement element= elements[i];
			IPackageFragmentRoot root= (IPackageFragmentRoot)element.getAncestor(IJavaElement.PACKAGE_FRAGMENT_ROOT);
			if (root != null && root.isArchive())
				return true;
		}
		return false;
	}
	
	public static boolean containsOnlyProjects(List elements){
		if (elements.isEmpty())
			return false;
		for(Iterator iter= elements.iterator(); iter.hasNext(); ) {
			if (! isProject(iter.next()))
				return false;
		}
		return true;
	}
	
	public static boolean isProject(Object element){
		return (element instanceof IJavaProject) || (element instanceof IProject);
	}

	public static boolean isInsideCompilationUnit(IJavaElement element) {
		return 	!(element instanceof ICompilationUnit) && 
				hasAncestorOfType(element, IJavaElement.COMPILATION_UNIT);
	}
	
	public static boolean isInsideClassFile(IJavaElement element) {
		return 	!(element instanceof IClassFile) && 
				hasAncestorOfType(element, IJavaElement.CLASS_FILE);
	}
	
	public static boolean hasAncestorOfType(IJavaElement element, int type){
		return element.getAncestor(type) != null;
	}
	
	/**
	 * May be <code>null</code>.
	 */
	public static ICompilationUnit getCompilationUnit(IJavaElement javaElement){
		if (javaElement instanceof ICompilationUnit)
			return (ICompilationUnit) javaElement;
		return (ICompilationUnit) javaElement.getAncestor(IJavaElement.COMPILATION_UNIT);
	}

	/**
	 * some of the returned elements may be <code>null</code>.
	 */
	public static ICompilationUnit[] getCompilationUnits(IJavaElement[] javaElements){
		ICompilationUnit[] result= new ICompilationUnit[javaElements.length];
		for (int i= 0; i < javaElements.length; i++) {
			result[i]= getCompilationUnit(javaElements[i]);
		}
		return result;
	}
		
	public static IResource getResource(IJavaElement element){
		if (element instanceof ICompilationUnit)
			return ((ICompilationUnit)element).getPrimary().getResource();
		else
			return element.getResource();
	}
	
	public static IResource[] getResources(IJavaElement[] elements) {
		IResource[] result= new IResource[elements.length];
		for (int i= 0; i < elements.length; i++) {
			result[i]= ReorgUtils.getResource(elements[i]);
		}
		return result;
	}
	
	public static String getName(IResource resource) {
		String pattern= createNamePattern(resource);
		String[] args= createNameArguments(resource);
		return Messages.format(pattern, args);
	}
	
	private static String createNamePattern(IResource resource) {
		switch(resource.getType()){
			case IResource.FILE:
				return RefactoringCoreMessages.ReorgUtils_0; 
			case IResource.FOLDER:
				return RefactoringCoreMessages.ReorgUtils_1; 
			case IResource.PROJECT:
				return RefactoringCoreMessages.ReorgUtils_2; 
			default:
				Assert.isTrue(false);
				return null;
		}
	}

	private static String[] createNameArguments(IResource resource) {
		return new String[]{resource.getName()};
	}

	public static String getName(IJavaElement element) throws JavaModelException {
		String pattern= createNamePattern(element);
		String[] args= createNameArguments(element);
		return Messages.format(pattern, args);
	}

	private static String[] createNameArguments(IJavaElement element) throws JavaModelException {
		switch(element.getElementType()){
			case IJavaElement.CLASS_FILE:
				return new String[]{element.getElementName()};
			case IJavaElement.COMPILATION_UNIT:
				return new String[]{element.getElementName()};
			case IJavaElement.FIELD:
				return new String[]{element.getElementName()};
			case IJavaElement.IMPORT_CONTAINER:
				return new String[0];
			case IJavaElement.IMPORT_DECLARATION:
				return new String[]{element.getElementName()};
			case IJavaElement.INITIALIZER:
				return new String[0];
			case IJavaElement.JAVA_PROJECT:
				return new String[]{element.getElementName()};
			case IJavaElement.METHOD:
				return new String[]{element.getElementName()};
			case IJavaElement.PACKAGE_DECLARATION:
				if (JavaElementUtil.isDefaultPackage(element))
					return new String[0];
				else
					return new String[]{element.getElementName()};
			case IJavaElement.PACKAGE_FRAGMENT:
				return new String[]{element.getElementName()};
			case IJavaElement.PACKAGE_FRAGMENT_ROOT:
				return new String[]{element.getElementName()};
			case IJavaElement.TYPE:
				IType type= (IType)element;
				String name= type.getElementName();
				if (name.length() == 0 && type.isAnonymous()) {
					String superclassName= Signature.getSimpleName(type.getSuperclassName());
					return new String[]{Messages.format(RefactoringCoreMessages.ReorgUtils_19, superclassName)}; 
				}
				return new String[]{element.getElementName()};
			default:
				Assert.isTrue(false);
				return null;
		}
	}

	private static String createNamePattern(IJavaElement element) throws JavaModelException {
		switch(element.getElementType()){
			case IJavaElement.CLASS_FILE:
				return RefactoringCoreMessages.ReorgUtils_3; 
			case IJavaElement.COMPILATION_UNIT:
				return RefactoringCoreMessages.ReorgUtils_4; 
			case IJavaElement.FIELD:
				return RefactoringCoreMessages.ReorgUtils_5; 
			case IJavaElement.IMPORT_CONTAINER:
				return RefactoringCoreMessages.ReorgUtils_6; 
			case IJavaElement.IMPORT_DECLARATION:
				return RefactoringCoreMessages.ReorgUtils_7; 
			case IJavaElement.INITIALIZER:
				return RefactoringCoreMessages.ReorgUtils_8; 
			case IJavaElement.JAVA_PROJECT:
				return RefactoringCoreMessages.ReorgUtils_9; 
			case IJavaElement.METHOD:
				if (((IMethod)element).isConstructor())
					return RefactoringCoreMessages.ReorgUtils_10; 
				else
					return RefactoringCoreMessages.ReorgUtils_11; 
			case IJavaElement.PACKAGE_DECLARATION:
				return RefactoringCoreMessages.ReorgUtils_12; 
			case IJavaElement.PACKAGE_FRAGMENT:
				if (JavaElementUtil.isDefaultPackage(element))
					return RefactoringCoreMessages.ReorgUtils_13; 
				else
					return RefactoringCoreMessages.ReorgUtils_14; 
			case IJavaElement.PACKAGE_FRAGMENT_ROOT:
				if (isSourceFolder(element))
					return RefactoringCoreMessages.ReorgUtils_15; 
				if (isClassFolder(element))
					return RefactoringCoreMessages.ReorgUtils_16; 
				return RefactoringCoreMessages.ReorgUtils_17; 
			case IJavaElement.TYPE:
				IType type= (IType)element;
				if (type.getElementName().length() == 0 && type.isAnonymous())
					return RefactoringCoreMessages.ReorgUtils_20; 
				return RefactoringCoreMessages.ReorgUtils_18; 
			default:
				Assert.isTrue(false);
				return null;
		}
	}

	public static IResource[] getResources(List elements) {
		List resources= new ArrayList(elements.size());
		for (Iterator iter= elements.iterator(); iter.hasNext();) {
			Object element= iter.next();
			if (element instanceof IResource)
				resources.add(element);
		}
		return (IResource[]) resources.toArray(new IResource[resources.size()]);
	}

	public static IJavaElement[] getJavaElements(List elements) {
		List resources= new ArrayList(elements.size());
		for (Iterator iter= elements.iterator(); iter.hasNext();) {
			Object element= iter.next();
			if (element instanceof IJavaElement)
				resources.add(element);
		}
		return (IJavaElement[]) resources.toArray(new IJavaElement[resources.size()]);
	}
	
	public static IWorkingSet[] getWorkingSets(List elements) {
		List result= new ArrayList(1);
		for (Iterator iter= elements.iterator(); iter.hasNext();) {
			Object element= iter.next();
			if (element instanceof IWorkingSet) {
				result.add(element);
			}
		}
		return (IWorkingSet[])result.toArray(new IWorkingSet[result.size()]);
	}
	
	public static boolean isDeletedFromEditor(IJavaElement elem) {
		if (! isInsideCompilationUnit(elem))
			return false;
		if (elem instanceof IMember && ((IMember)elem).isBinary())
			return false;
		ICompilationUnit cu= ReorgUtils.getCompilationUnit(elem);
		if (cu == null)
			return false;
		ICompilationUnit wc= cu;
		// TODO have to understand if this method is needed any longer. Since
		// with the new working copy support a element is never deleted from
		// the editor if we have a primary working copy.
		if (cu.equals(wc))
			return false;
		IJavaElement wcElement= JavaModelUtil.findInCompilationUnit(wc, elem);
		return wcElement == null || ! wcElement.exists();
	}
	
	public static boolean hasSourceAvailable(IMember member) throws JavaModelException{
		return ! member.isBinary() || 
				(member.getSourceRange() != null && ! fgUnknownRange.equals(member.getSourceRange()));
	}
	
	public static IResource[] setMinus(IResource[] setToRemoveFrom, IResource[] elementsToRemove) {
		Set setMinus= new HashSet(setToRemoveFrom.length - setToRemoveFrom.length);
		setMinus.addAll(Arrays.asList(setToRemoveFrom));
		setMinus.removeAll(Arrays.asList(elementsToRemove));
		return (IResource[]) setMinus.toArray(new IResource[setMinus.size()]);		
	}

	public static IJavaElement[] setMinus(IJavaElement[] setToRemoveFrom, IJavaElement[] elementsToRemove) {
		Set setMinus= new HashSet(setToRemoveFrom.length - setToRemoveFrom.length);
		setMinus.addAll(Arrays.asList(setToRemoveFrom));
		setMinus.removeAll(Arrays.asList(elementsToRemove));
		return (IJavaElement[]) setMinus.toArray(new IJavaElement[setMinus.size()]);		
	}
	
	public static IJavaElement[] union(IJavaElement[] set1, IJavaElement[] set2) {
		List union= new ArrayList(set1.length + set2.length);//use lists to avoid sequence problems
		addAll(set1, union);
		addAll(set2, union);
		return (IJavaElement[]) union.toArray(new IJavaElement[union.size()]);
	}	

	public static IResource[] union(IResource[] set1, IResource[] set2) {
		List union= new ArrayList(set1.length + set2.length);//use lists to avoid sequence problems
		addAll(ReorgUtils.getNotNulls(set1), union);
		addAll(ReorgUtils.getNotNulls(set2), union);
		return (IResource[]) union.toArray(new IResource[union.size()]);
	}	

	private static void addAll(Object[] array, List list) {
		for (int i= 0; i < array.length; i++) {
			if (! list.contains(array[i]))
				list.add(array[i]);
		}
	}

	public static Set union(Set set1, Set set2){
		Set union= new HashSet(set1.size() + set2.size());
		union.addAll(set1);
		union.addAll(set2);
		return union;
	}

	public static IType[] getMainTypes(IJavaElement[] javaElements) throws JavaModelException {
		List result= new ArrayList();
		for (int i= 0; i < javaElements.length; i++) {
			IJavaElement element= javaElements[i];
			if (element instanceof IType && JavaElementUtil.isMainType((IType)element))
				result.add(element);
		}
		return (IType[]) result.toArray(new IType[result.size()]);
	}
	
	public static IFolder[] getFolders(IResource[] resources) {
		Set result= getResourcesOfType(resources, IResource.FOLDER);
		return (IFolder[]) result.toArray(new IFolder[result.size()]);
	}

	public static IFile[] getFiles(IResource[] resources) {
		Set result= getResourcesOfType(resources, IResource.FILE);
		return (IFile[]) result.toArray(new IFile[result.size()]);
	}
		
	//the result can be cast down to the requested type array
	public static Set getResourcesOfType(IResource[] resources, int typeMask){
		Set result= new HashSet(resources.length);
		for (int i= 0; i < resources.length; i++) {
			if (isOfType(resources[i], typeMask))
				result.add(resources[i]);
		}
		return result;
	}
	
	//the result can be cast down to the requested type array
	//type is _not_ a mask	
	public static List getElementsOfType(IJavaElement[] javaElements, int type){
		List result= new ArrayList(javaElements.length);
		for (int i= 0; i < javaElements.length; i++) {
			if (isOfType(javaElements[i], type))
				result.add(javaElements[i]);
		}
		return result;
	}

	public static boolean hasElementsNotOfType(IResource[] resources, int typeMask) {
		for (int i= 0; i < resources.length; i++) {
			IResource resource= resources[i];
			if (resource != null && ! isOfType(resource, typeMask))
				return true;
		}
		return false;
	}

	//type is _not_ a mask	
	public static boolean hasElementsNotOfType(IJavaElement[] javaElements, int type) {
		for (int i= 0; i < javaElements.length; i++) {
			IJavaElement element= javaElements[i];
			if (element != null && ! isOfType(element, type))
				return true;
		}
		return false;
	}
	
	//type is _not_ a mask	
	public static boolean hasElementsOfType(IJavaElement[] javaElements, int type) {
		for (int i= 0; i < javaElements.length; i++) {
			IJavaElement element= javaElements[i];
			if (element != null && isOfType(element, type))
				return true;
		}
		return false;
	}

	public static boolean hasElementsOfType(IJavaElement[] javaElements, int[] types) {
		for (int i= 0; i < types.length; i++) {
			if (hasElementsOfType(javaElements, types[i])) return true;
		}
		return false;
	}

	public static boolean hasElementsOfType(IResource[] resources, int typeMask) {
		for (int i= 0; i < resources.length; i++) {
			IResource resource= resources[i];
			if (resource != null && isOfType(resource, typeMask))
				return true;
		}
		return false;
	}

	private static boolean isOfType(IJavaElement element, int type) {
		return element.getElementType() == type;//this is _not_ a mask
	}
		
	private static boolean isOfType(IResource resource, int type) {
		return resource != null && isFlagSet(resource.getType(), type);
	}
		
	private static boolean isFlagSet(int flags, int flag){
		return (flags & flag) != 0;
	}

	public static boolean isSourceFolder(IJavaElement javaElement) throws JavaModelException {
		return (javaElement instanceof IPackageFragmentRoot) &&
				((IPackageFragmentRoot)javaElement).getKind() == IPackageFragmentRoot.K_SOURCE;
	}
	
	public static boolean isClassFolder(IJavaElement javaElement) throws JavaModelException {
		return (javaElement instanceof IPackageFragmentRoot) &&
				((IPackageFragmentRoot)javaElement).getKind() == IPackageFragmentRoot.K_BINARY;
	}
	
	public static boolean isPackageFragmentRoot(IJavaProject javaProject) throws JavaModelException{
		return getCorrespondingPackageFragmentRoot(javaProject) != null;
	}
	
	private static boolean isPackageFragmentRootCorrespondingToProject(IPackageFragmentRoot root) {
		return root.getResource() instanceof IProject;
	}

	public static IPackageFragmentRoot getCorrespondingPackageFragmentRoot(IJavaProject p) throws JavaModelException {
		IPackageFragmentRoot[] roots= p.getPackageFragmentRoots();
		for (int i= 0; i < roots.length; i++) {
			if (isPackageFragmentRootCorrespondingToProject(roots[i]))
				return roots[i];
		}
		return null;
	}
		
	public static boolean containsLinkedResources(IResource[] resources){
		for (int i= 0; i < resources.length; i++) {
			if (resources[i] != null && resources[i].isLinked()) return true;
		}
		return false;
	}
	
	public static boolean containsLinkedResources(IJavaElement[] javaElements){
		for (int i= 0; i < javaElements.length; i++) {
			IResource res= getResource(javaElements[i]);
			if (res != null && res.isLinked()) return true;
		}
		return false;
	}

	public static boolean canBeDestinationForLinkedResources(IResource resource) {
		return resource.isAccessible() && resource instanceof IProject;
	}

	public static boolean canBeDestinationForLinkedResources(IJavaElement javaElement) {
		if (javaElement instanceof IPackageFragmentRoot){
			return isPackageFragmentRootCorrespondingToProject((IPackageFragmentRoot)javaElement);
		} else if (javaElement instanceof IJavaProject){
			return true;//XXX ???
		} else return false;
	}
	
	public static boolean isParentInWorkspaceOrOnDisk(IPackageFragment pack, IPackageFragmentRoot root){
		if (pack == null)
			return false;		
		IJavaElement packParent= pack.getParent();
		if (packParent == null)
			return false;		
		if (packParent.equals(root))	
			return true;
		IResource packageResource= ResourceUtil.getResource(pack);
		IResource packageRootResource= ResourceUtil.getResource(root);
		return isParentInWorkspaceOrOnDisk(packageResource, packageRootResource);
	}

	public static boolean isParentInWorkspaceOrOnDisk(IPackageFragmentRoot root, IJavaProject javaProject){
		if (root == null)
			return false;		
		IJavaElement rootParent= root.getParent();
		if (rootParent == null)
			return false;		
		if (rootParent.equals(root))	
			return true;
		IResource packageResource= ResourceUtil.getResource(root);
		IResource packageRootResource= ResourceUtil.getResource(javaProject);
		return isParentInWorkspaceOrOnDisk(packageResource, packageRootResource);
	}

	public static boolean isParentInWorkspaceOrOnDisk(ICompilationUnit cu, IPackageFragment dest){
		if (cu == null)
			return false;
		IJavaElement cuParent= cu.getParent();
		if (cuParent == null)
			return false;
		if (cuParent.equals(dest))	
			return true;
		IResource cuResource= ResourceUtil.getResource(cu);
		IResource packageResource= ResourceUtil.getResource(dest);
		return isParentInWorkspaceOrOnDisk(cuResource, packageResource);
	}
	
	public static boolean isParentInWorkspaceOrOnDisk(IResource res, IResource maybeParent){
		if (res == null)
			return false;
		return areEqualInWorkspaceOrOnDisk(res.getParent(), maybeParent);
	}
	
	public static boolean areEqualInWorkspaceOrOnDisk(IResource r1, IResource r2){
		if (r1 == null || r2 == null)
			return false;
		if (r1.equals(r2))
			return true;
		URI r1Location= r1.getLocationURI();
		URI r2Location= r2.getLocationURI();
		if (r1Location == null || r2Location == null)
			return false;
		return r1Location.equals(r2Location);
	}
	
	public static IResource[] getNotNulls(IResource[] resources) {
		Collection result= new ArrayList(resources.length);
		for (int i= 0; i < resources.length; i++) {
			IResource resource= resources[i];
			if (resource != null && ! result.contains(resource))
				result.add(resource);
		}
		return (IResource[]) result.toArray(new IResource[result.size()]);
	}
	
	public static IResource[] getNotLinked(IResource[] resources) {
		Collection result= new ArrayList(resources.length);
		for (int i= 0; i < resources.length; i++) {
			IResource resource= resources[i];
			if (resource != null && ! result.contains(resource) && ! resource.isLinked())
				result.add(resource);
		}
		return (IResource[]) result.toArray(new IResource[result.size()]);
	}
	
	/* List<IJavaElement> javaElements
	 * return ICompilationUnit -> List<IJavaElement>
	 */
	public static Map groupByCompilationUnit(List javaElements){
		Map result= new HashMap();
		for (Iterator iter= javaElements.iterator(); iter.hasNext();) {
			IJavaElement element= (IJavaElement) iter.next();
			ICompilationUnit cu= ReorgUtils.getCompilationUnit(element);
			if (cu != null){
				if (! result.containsKey(cu))
					result.put(cu, new ArrayList(1));
				((List)result.get(cu)).add(element);
			}
		}
		return result;
	}
	
	public static void splitIntoJavaElementsAndResources(Object[] elements, List javaElementResult, List resourceResult) {
		for (int i= 0; i < elements.length; i++) {
			Object element= elements[i];
			if (element instanceof IJavaElement) {
				javaElementResult.add(element);
			} else if (element instanceof IResource) {
				IResource resource= (IResource)element;
				IJavaElement jElement= JavaCore.create(resource);
				if (jElement != null && jElement.exists())
					javaElementResult.add(jElement);
				else
					resourceResult.add(resource);
			}
		}
	}

	public static boolean containsElementOrParent(Set elements, IJavaElement element) {
		if (elements.contains(element))
			return true;
		IJavaElement parent= element.getParent();
		while (parent != null) {
			if (elements.contains(parent))
				return true;
			parent= parent.getParent();
		}
		return false;
	}

	public static boolean containsElementOrParent(Set elements, IResource element) {
		if (elements.contains(element))
			return true;
		IResource parent= element.getParent();
		while (parent != null) {
			if (elements.contains(parent))
				return true;
			IJavaElement parentAsJavaElement= JavaCore.create(parent);
			if (parentAsJavaElement != null && parentAsJavaElement.exists() && elements.contains(parentAsJavaElement))
				return true;
			parent= parent.getParent();
		}
		return false;
	}
}
