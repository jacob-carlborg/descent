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
package descent.internal.corext.refactoring.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.Assert;

import org.eclipse.core.resources.ResourcesPlugin;

import descent.core.IClasspathEntry;
import descent.core.ICompilationUnit;
import descent.core.IField;
import descent.core.IJavaElement;
import descent.core.IJavaProject;
import descent.core.IMember;
import descent.core.IMethod;
import descent.core.IPackageFragment;
import descent.core.IPackageFragmentRoot;
import descent.core.ISourceReference;
import descent.core.IType;
import descent.core.JavaCore;
import descent.core.JavaModelException;
import descent.core.Signature;

import descent.internal.corext.SourceRange;
import descent.internal.corext.refactoring.RefactoringCoreMessages;
import descent.internal.corext.util.JavaModelUtil;

public class JavaElementUtil {
	
	//no instances
	private JavaElementUtil(){
	}
	
	public static String createMethodSignature(IMethod method){
		// TODO JDT signature
//		try {
//			return Signature.toString(method.getSignature(), method.getElementName(), method.getParameterNames(), false, ! method.isConstructor());
//		} catch(JavaModelException e) {
			return method.getElementName(); //fallback
//		}
	}
	
	public static String createFieldSignature(IField field){
		return JavaModelUtil.getFullyQualifiedName(field.getDeclaringType()) + "." + field.getElementName(); //$NON-NLS-1$
	}
	
	public static String createSignature(IMember member){
		switch (member.getElementType()){
			case IJavaElement.FIELD:
				return createFieldSignature((IField)member);
			case IJavaElement.TYPE:
				return JavaModelUtil.getFullyQualifiedName(((IType)member));
			case IJavaElement.INITIALIZER:
				return RefactoringCoreMessages.JavaElementUtil_initializer; 
			case IJavaElement.METHOD:
				return createMethodSignature((IMethod)member);				
			default:
				Assert.isTrue(false);
				return null;	
		}
	}
	
	public static IJavaElement[] getElementsOfType(IJavaElement[] elements, int type){
		Set result= new HashSet(elements.length);
		for (int i= 0; i < elements.length; i++) {
			IJavaElement element= elements[i];
			if (element.getElementType() == type)
				result.add(element);
		}
		return (IJavaElement[]) result.toArray(new IJavaElement[result.size()]);
	}

	public static IType getMainType(ICompilationUnit cu) throws JavaModelException{
		IType[] types= cu.getTypes();
		for (int i = 0; i < types.length; i++) {
			if (isMainType(types[i]))
				return types[i];
		}
		return null;
	}
	
	public static boolean isMainType(IType type) throws JavaModelException{
		if (! type.exists())	
			return false;

		if (type.isBinary())
			return false;
			
		if (type.getCompilationUnit() == null)
			return false;
		
		if (type.getDeclaringType() != null)
			return false;
		
		return isPrimaryType(type) || isCuOnlyType(type);
	}


	private static boolean isPrimaryType(IType type){
		return type.equals(type.getCompilationUnit().findPrimaryType());
	}


	private static boolean isCuOnlyType(IType type) throws JavaModelException{
		return type.getCompilationUnit().getTypes().length == 1;
	}

	/** see descent.internal.core.JavaElement#isAncestorOf(descent.core.IJavaElement) */
	public static boolean isAncestorOf(IJavaElement ancestor, IJavaElement child) {
		IJavaElement parent= child.getParent();
		while (parent != null && !parent.equals(ancestor)) {
			parent= parent.getParent();
		}
		return parent != null;
	}
	
	public static IMethod[] getAllConstructors(IType type) throws JavaModelException {
		if (JavaModelUtil.isInterfaceOrAnnotation(type))
			return new IMethod[0];
		List result= new ArrayList();
		IMethod[] methods= type.getMethods();
		for (int i= 0; i < methods.length; i++) {
			IMethod iMethod= methods[i];
			if (iMethod.isConstructor())
				result.add(iMethod);
		}
		return (IMethod[]) result.toArray(new IMethod[result.size()]);
	}

	/**
	 * Returns an array of projects that have the specified root on their
	 * classpaths.
	 */
	public static IJavaProject[] getReferencingProjects(IPackageFragmentRoot root) throws JavaModelException {
		IClasspathEntry cpe= root.getRawClasspathEntry();
		IJavaProject myProject= root.getJavaProject();
		IJavaProject[] allJavaProjects= JavaCore.create(ResourcesPlugin.getWorkspace().getRoot()).getJavaProjects();
		List result= new ArrayList(allJavaProjects.length);
		for (int i= 0; i < allJavaProjects.length; i++) {
			IJavaProject project= allJavaProjects[i];
			if (project.equals(myProject))
				continue;
			IPackageFragmentRoot[] roots= project.findPackageFragmentRoots(cpe);
			if (roots.length > 0)
				result.add(project);
		}
		return (IJavaProject[]) result.toArray(new IJavaProject[result.size()]);
	}	
	
	public static IMember[] merge(IMember[] a1, IMember[] a2) {
		// Don't use hash sets since ordering is important for some refactorings.
		List result= new ArrayList(a1.length + a2.length);
		for (int i= 0; i < a1.length; i++) {
			IMember member= a1[i];
			if (!result.contains(member))
				result.add(member);
		}
		for (int i= 0; i < a2.length; i++) {
			IMember member= a2[i];
			if (!result.contains(member))
				result.add(member);
		}
		return (IMember[]) result.toArray(new IMember[result.size()]);
	}

	public static boolean isDefaultPackage(Object element) {
		return (element instanceof IPackageFragment) && ((IPackageFragment)element).isDefaultPackage();
	}
	
	/**
	 * @param pack a package fragment
	 * @return an array containing the given package and all subpackages 
	 * @throws JavaModelException 
	 */
	public static IPackageFragment[] getPackageAndSubpackages(IPackageFragment pack) throws JavaModelException {
		if (pack.isDefaultPackage())
			return new IPackageFragment[] { pack };
		
		IPackageFragmentRoot root= (IPackageFragmentRoot) pack.getParent();
		IJavaElement[] allPackages= root.getChildren();
		ArrayList subpackages= new ArrayList();
		subpackages.add(pack);
		String prefix= pack.getElementName() + '.';
		for (int i= 0; i < allPackages.length; i++) {
			IPackageFragment currentPackage= (IPackageFragment) allPackages[i];
			if (currentPackage.getElementName().startsWith(prefix))
				subpackages.add(currentPackage);
		}
		return (IPackageFragment[]) subpackages.toArray(new IPackageFragment[subpackages.size()]);
	}
	
	/**
	 * @param pack the package fragment; may not be null
	 * @return the parent package fragment, or null if the given package fragment is the default package or a top level package
	 */
	public static IPackageFragment getParentSubpackage(IPackageFragment pack) {
		if (pack.isDefaultPackage())
			return null;
		
		final int index= pack.getElementName().lastIndexOf('.');
		if (index == -1)
			return null;

		final IPackageFragmentRoot root= (IPackageFragmentRoot) pack.getParent();
		final String newPackageName= pack.getElementName().substring(0, index);
		final IPackageFragment parent= root.getPackageFragment(newPackageName);
		if (parent.exists())
			return parent;
		else
			return null;
	}
	
	public static IMember[] sortByOffset(IMember[] members){
		Comparator comparator= new Comparator(){
			public int compare(Object o1, Object o2){
				try{
					return ((IMember) o1).getNameRange().getOffset() - ((IMember) o2).getNameRange().getOffset();
				} catch (JavaModelException e){
					return 0;
				}	
			}
		};
		Arrays.sort(members, comparator);
		return members;
	}
	
	public static boolean isSourceAvailable(ISourceReference sourceReference) {
		try {
			return SourceRange.isAvailable(sourceReference.getSourceRange());
		} catch (JavaModelException e) {
			return false;
		}
	}
}
