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
package descent.internal.corext.codemanipulation;

import descent.core.ICompilationUnit;
import descent.core.IJavaElement;
import descent.core.IPackageFragment;
import descent.core.IType;
import descent.core.JavaModelException;
import descent.core.dom.CompilationUnit;
import descent.core.dom.IBinding;
import descent.core.dom.ITypeBinding;
import descent.core.dom.Name;
import descent.core.dom.rewrite.ImportRewrite;
import descent.core.dom.rewrite.ImportRewrite.ImportRewriteContext;
import descent.internal.corext.util.JavaModelUtil;

/**
 * This {@link ImportRewriteContext} is aware of all the types visible in 
 * <code>compilationUnit</code> at <code>position</code>.
 */
public class ContextSensitiveImportRewriteContext extends ImportRewriteContext {
	
	private final CompilationUnit fCompilationUnit;
	private final int fPosition;
	private IBinding[] fDeclarationsInScope;
	private Name[] fImportedNames;
	private final ImportRewrite fImportRewrite;
	
	public ContextSensitiveImportRewriteContext(CompilationUnit compilationUnit, int position, ImportRewrite importRewrite) {
		fCompilationUnit= compilationUnit;
		fPosition= position;
		fImportRewrite= importRewrite;
		fDeclarationsInScope= null;
		fImportedNames= null;
	}

	public int findInContext(String qualifier, String name, int kind) {
		IBinding[] declarationsInScope= getDeclarationsInScope();
		for (int i= 0; i < declarationsInScope.length; i++) {
			if (declarationsInScope[i] instanceof ITypeBinding) {
				ITypeBinding typeBinding= (ITypeBinding)declarationsInScope[i];
				if (isSameType(typeBinding, qualifier, name)) {
					return RES_NAME_FOUND;
				} else if (isConflicting(typeBinding, name)) {
					return RES_NAME_CONFLICT;
				}
			} else if (declarationsInScope[i] != null) {
				if (isConflicting(declarationsInScope[i], name)) {
					return RES_NAME_CONFLICT;
				}
			}
		}
		
		Name[] names= getImportedNames();
		for (int i= 0; i < names.length; i++) {
			IBinding binding= names[i].resolveBinding();
			if (binding instanceof ITypeBinding) {
				ITypeBinding typeBinding= (ITypeBinding)binding;
				if (isConflictingType(typeBinding, qualifier, name)) {
					return RES_NAME_CONFLICT;
				}
			}
		}
		
		/* TODO JDT UI import rewrite 
		List list= fCompilationUnit.types();
		for (Iterator iter= list.iterator(); iter.hasNext();) {
			AbstractTypeDeclaration type= (AbstractTypeDeclaration)iter.next();
			ITypeBinding binding= type.resolveBinding();
			if (binding != null) {
				if (isSameType(binding, qualifier, name)) {
					return RES_NAME_FOUND;
				} else {
					if (containsDeclaration(binding, qualifier, name))
						return RES_NAME_CONFLICT;
				}
			}
		}
		*/
		
		String[] addedImports= fImportRewrite.getAddedImports();
		String qualifiedName= JavaModelUtil.concatenateName(qualifier, name);
		for (int i= 0; i < addedImports.length; i++) {
			String addedImport= addedImports[i];
			if (qualifiedName.equals(addedImport)) {
				return RES_NAME_FOUND;
			} else {
				if (isConflicting(name, addedImport))
					return RES_NAME_CONFLICT;
			}
		}
		
		if (qualifier.equals("java.lang")) { //$NON-NLS-1$
			//No explicit import statement required
			IJavaElement parent= fCompilationUnit.getJavaElement().getParent();
			if (parent instanceof IPackageFragment) {
				IPackageFragment packageFragment= (IPackageFragment)parent;
				try {
					ICompilationUnit[] compilationUnits= packageFragment.getCompilationUnits();
					for (int i= 0; i < compilationUnits.length; i++) {
						ICompilationUnit cu= compilationUnits[i];
						IType[] allTypes= cu.getAllTypes();
						for (int j= 0; j < allTypes.length; j++) {
							IType type= allTypes[j];
							String packageTypeName= type.getFullyQualifiedName();
							if (isConflicting(name, packageTypeName))
								return RES_NAME_CONFLICT;
						}
					}
				} catch (JavaModelException e) {
				}
			}
		}
		
		return RES_NAME_UNKNOWN;
	}

	private boolean isConflicting(String name, String importt) {
		int index= importt.lastIndexOf('.');
		String importedName;
		if (index == -1) {
			importedName= importt;
		} else {
			importedName= importt.substring(index + 1, importt.length());
		}
		if (importedName.equals(name)) {
			return true;
		}
		return false;
	}
	
	private boolean containsDeclaration(ITypeBinding binding, String qualifier, String name) {
		ITypeBinding[] declaredTypes= binding.getDeclaredTypes();
		for (int i= 0; i < declaredTypes.length; i++) {
			ITypeBinding childBinding= declaredTypes[i];
			if (isSameType(childBinding, qualifier, name)) {
				return true;
			} else {
				if (containsDeclaration(childBinding, qualifier, name)) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isConflicting(IBinding binding, String name) {
		return binding.getName().equals(name);
	}

	private boolean isSameType(ITypeBinding binding, String qualifier, String name) {
		String qualifiedName= JavaModelUtil.concatenateName(qualifier, name);
		return binding.getQualifiedName().equals(qualifiedName);
	}
	
	private boolean isConflictingType(ITypeBinding binding, String qualifier, String name) {
		return false;
		// TODO JDT Bindings
//		binding= binding.getTypeDeclaration();
//		return !isSameType(binding, qualifier, name) && isConflicting(binding, name);
	}
	
	private IBinding[] getDeclarationsInScope() {
		/* TODO JDT UI import rewrite
		if (fDeclarationsInScope == null) {
			ScopeAnalyzer analyzer= new ScopeAnalyzer(fCompilationUnit);
			fDeclarationsInScope= analyzer.getDeclarationsInScope(fPosition, ScopeAnalyzer.METHODS | ScopeAnalyzer.TYPES | ScopeAnalyzer.VARIABLES);
		}
		*/
		return fDeclarationsInScope;
	}
	
	private Name[] getImportedNames() {
		/* TODO JDT UI import rewrite
		if (fImportedNames == null) {
			IJavaProject project= null;
			IJavaElement javaElement= fCompilationUnit.getJavaElement();
			if (javaElement != null)
				project= javaElement.getJavaProject();
			
			List imports= new ArrayList();
			ImportReferencesCollector.collect(fCompilationUnit, project, null, imports, null);
			fImportedNames= (Name[])imports.toArray(new Name[imports.size()]);
		}
		*/
		return fImportedNames;
	}
}
