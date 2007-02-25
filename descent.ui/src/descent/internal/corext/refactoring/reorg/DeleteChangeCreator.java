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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.text.edits.TextEdit;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.core.filebuffers.ITextFileBuffer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;

import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.NullChange;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;

import descent.core.IClassFile;
import descent.core.ICompilationUnit;
import descent.core.IJavaElement;
import descent.core.IPackageFragment;
import descent.core.IPackageFragmentRoot;
import descent.core.ISourceManipulation;
import descent.core.dom.CompilationUnit;
import descent.core.dom.rewrite.ASTRewrite;

import descent.internal.corext.refactoring.Checks;
import descent.internal.corext.refactoring.RefactoringCoreMessages;
import descent.internal.corext.refactoring.changes.DeleteFileChange;
import descent.internal.corext.refactoring.changes.DeleteFolderChange;
import descent.internal.corext.refactoring.changes.DeleteFromClasspathChange;
import descent.internal.corext.refactoring.changes.DeletePackageFragmentRootChange;
import descent.internal.corext.refactoring.changes.DeleteSourceManipulationChange;
import descent.internal.corext.refactoring.changes.DynamicValidationStateChange;
import descent.internal.corext.refactoring.changes.TextChangeCompatibility;
import descent.internal.corext.refactoring.structure.CompilationUnitRewrite;
import descent.internal.corext.refactoring.util.RefactoringASTParser;
import descent.internal.corext.refactoring.util.RefactoringFileBuffers;
import descent.internal.corext.refactoring.util.TextChangeManager;


class DeleteChangeCreator {
	private DeleteChangeCreator() {
		//private
	}
	
	static Change createDeleteChange(TextChangeManager manager, IResource[] resources, IJavaElement[] javaElements, String changeName) throws CoreException {
		final DynamicValidationStateChange result= new DynamicValidationStateChange(changeName) {
			public Change perform(IProgressMonitor pm) throws CoreException {
				super.perform(pm);
				return null;
			}
		};
		for (int i= 0; i < javaElements.length; i++) {
			IJavaElement element= javaElements[i];
			if (! ReorgUtils.isInsideCompilationUnit(element))
				result.add(createDeleteChange(element));
		}

		for (int i= 0; i < resources.length; i++) {
			result.add(createDeleteChange(resources[i]));
		}
		
		Map grouped= ReorgUtils.groupByCompilationUnit(getElementsSmallerThanCu(javaElements));
		if (grouped.size() != 0 ){
			Assert.isNotNull(manager);
			for (Iterator iter= grouped.keySet().iterator(); iter.hasNext();) {
				ICompilationUnit cu= (ICompilationUnit) iter.next();
				result.add(createDeleteChange(cu, (List)grouped.get(cu), manager));
			}
		}

		return result;
	}
	
	private static Change createDeleteChange(IResource resource) {
		Assert.isTrue(! (resource instanceof IWorkspaceRoot));//cannot be done
		Assert.isTrue(! (resource instanceof IProject)); //project deletion is handled by the workbench
		if (resource instanceof IFile)
			return new DeleteFileChange((IFile)resource, true);
		if (resource instanceof IFolder)
			return new DeleteFolderChange((IFolder)resource, true);
		Assert.isTrue(false);//there're no more kinds
		return null;
	}

	/*
	 * List<IJavaElement> javaElements
	 */
	private static Change createDeleteChange(ICompilationUnit cu, List javaElements, TextChangeManager manager) throws CoreException {
		CompilationUnit cuNode= RefactoringASTParser.parseWithASTProvider(cu, false, null);
		CompilationUnitRewrite rewriter= new CompilationUnitRewrite(cu, cuNode);
		IJavaElement[] elements= (IJavaElement[]) javaElements.toArray(new IJavaElement[javaElements.size()]);
		ASTNodeDeleteUtil.markAsDeleted(elements, rewriter, null);
		return addTextEditFromRewrite(manager, cu, rewriter.getASTRewrite());
	}

	private static TextChange addTextEditFromRewrite(TextChangeManager manager, ICompilationUnit cu, ASTRewrite rewrite) throws CoreException {
		try {
			ITextFileBuffer buffer= RefactoringFileBuffers.acquire(cu);
			TextEdit resultingEdits= rewrite.rewriteAST(buffer.getDocument(), cu.getJavaProject().getOptions(true));
			TextChange textChange= manager.get(cu);
			if (textChange instanceof TextFileChange) {
				TextFileChange tfc= (TextFileChange) textChange;
				if (cu.isWorkingCopy())
					tfc.setSaveMode(TextFileChange.LEAVE_DIRTY);
			}
			String message= RefactoringCoreMessages.DeleteChangeCreator_1; 
			TextChangeCompatibility.addTextEdit(textChange, message, resultingEdits);
			return textChange;
		} finally {
			RefactoringFileBuffers.release(cu);
		}
	}

	//List<IJavaElement>
	private static List getElementsSmallerThanCu(IJavaElement[] javaElements){
		List result= new ArrayList();
		for (int i= 0; i < javaElements.length; i++) {
			IJavaElement element= javaElements[i];
			if (ReorgUtils.isInsideCompilationUnit(element))
				result.add(element);
		}
		return result;
	}

	private static Change createDeleteChange(IJavaElement javaElement) {
		Assert.isTrue(! ReorgUtils.isInsideCompilationUnit(javaElement));
		
		switch(javaElement.getElementType()){
			case IJavaElement.PACKAGE_FRAGMENT_ROOT:
				return createPackageFragmentRootDeleteChange((IPackageFragmentRoot)javaElement);

			case IJavaElement.PACKAGE_FRAGMENT:
				return createSourceManipulationDeleteChange((IPackageFragment)javaElement);

			case IJavaElement.COMPILATION_UNIT:
				return createSourceManipulationDeleteChange((ICompilationUnit)javaElement);

			case IJavaElement.CLASS_FILE:
				//if this assert fails, it means that a precondition is missing
				Assert.isTrue(((IClassFile)javaElement).getResource() instanceof IFile);
				return createDeleteChange(((IClassFile)javaElement).getResource());

			case IJavaElement.JAVA_MODEL: //cannot be done
				Assert.isTrue(false);
				return null;

			case IJavaElement.JAVA_PROJECT: //handled differently
				Assert.isTrue(false);
				return null;

			case IJavaElement.TYPE:
			case IJavaElement.FIELD:
			case IJavaElement.METHOD:
			case IJavaElement.INITIALIZER:
			case IJavaElement.PACKAGE_DECLARATION:
			case IJavaElement.IMPORT_CONTAINER:
			case IJavaElement.IMPORT_DECLARATION:
				Assert.isTrue(false);//not done here
			default:
				Assert.isTrue(false);//there's no more kinds
				return new NullChange();
		}
	}

	private static Change createSourceManipulationDeleteChange(ISourceManipulation element) {
		//XXX workaround for bug 31384, in case of linked ISourceManipulation delete the resource
		if (element instanceof ICompilationUnit || element instanceof IPackageFragment){
			IResource resource;
			if (element instanceof ICompilationUnit)
				resource= ReorgUtils.getResource((ICompilationUnit)element);
			else 
				resource= ((IPackageFragment)element).getResource();
			if (resource != null && resource.isLinked())
				return createDeleteChange(resource);
		}
		return new DeleteSourceManipulationChange(element, true);
	}
	
	private static Change createPackageFragmentRootDeleteChange(IPackageFragmentRoot root) {
		IResource resource= root.getResource();
		if (resource != null && resource.isLinked()){
			//XXX using this code is a workaround for jcore bug 31998
			//jcore cannot handle linked stuff
			//normally, we should always create DeletePackageFragmentRootChange
			CompositeChange composite= new DynamicValidationStateChange(RefactoringCoreMessages.DeleteRefactoring_delete_package_fragment_root); 
	
			composite.add(new DeleteFromClasspathChange(root));
			Assert.isTrue(! Checks.isClasspathDelete(root));//checked in preconditions
			composite.add(createDeleteChange(resource));
	
			return composite;
		} else {
			Assert.isTrue(! root.isExternal());
			// TODO remove the query argument
			return new DeletePackageFragmentRootChange(root, true, null); 
		}
	}
}
