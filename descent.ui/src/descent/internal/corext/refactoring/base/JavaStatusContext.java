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
package descent.internal.corext.refactoring.base;

import descent.core.IClassFile;
import descent.core.ICompilationUnit;
import descent.core.IImportDeclaration;
import descent.core.IMember;
import descent.core.IMethod;
import descent.core.ISourceRange;
import descent.core.JavaModelException;
import descent.core.dom.ASTNode;
import descent.core.dom.IMethodBinding;

import descent.internal.corext.SourceRange;
import descent.internal.corext.dom.Selection;
import org.eclipse.ltk.core.refactoring.RefactoringStatusContext;

/**
 * A Java element context that can be used to annotate a </code>RefactoringStatusEntry<code> 
 * with detailed information about an error detected in an <code>IJavaElement</code>.
 */
public abstract class JavaStatusContext extends RefactoringStatusContext {

	private static class MemberSourceContext extends JavaStatusContext {
		private IMember fMember;
		private MemberSourceContext(IMember member) {
			fMember= member;
		}
		public boolean isBinary() {
			return fMember.isBinary();
		}
		public ICompilationUnit getCompilationUnit() {
			return fMember.getCompilationUnit();
		}
		public IClassFile getClassFile() {
			return fMember.getClassFile();
		}
		public ISourceRange getSourceRange() {
			try {
				return fMember.getSourceRange();
			} catch (JavaModelException e) {
				return new SourceRange(0,0);
			}
		}
	}
	
	private static class ImportDeclarationSourceContext extends JavaStatusContext {
		private IImportDeclaration fImportDeclartion;
		private ImportDeclarationSourceContext(IImportDeclaration declaration) {
			fImportDeclartion= declaration;
		}
		public boolean isBinary() {
			return false;
		}
		public ICompilationUnit getCompilationUnit() {
			return (ICompilationUnit)fImportDeclartion.getParent().getParent();
		}
		public IClassFile getClassFile() {
			return null;
		}
		public ISourceRange getSourceRange() {
			try {
				return fImportDeclartion.getSourceRange();
			} catch (JavaModelException e) {
				return new SourceRange(0,0);
			}
		}
	}
	
	private static class CompilationUnitSourceContext extends JavaStatusContext {
		private ICompilationUnit fCUnit;
		private ISourceRange fSourceRange;
		private CompilationUnitSourceContext(ICompilationUnit cunit, ISourceRange range) {
			fCUnit= cunit;
			fSourceRange= range;
			if (fSourceRange == null)
				fSourceRange= new SourceRange(0,0);
		}
		public boolean isBinary() {
			return false;
		}
		public ICompilationUnit getCompilationUnit() {
			return fCUnit;
		}
		public IClassFile getClassFile() {
			return null;
		}
		public ISourceRange getSourceRange() {
			return fSourceRange;
		}
		public String toString() {
			return getSourceRange() + " in " + super.toString(); //$NON-NLS-1$
		}
	}

	private static class ClassFileSourceContext extends JavaStatusContext {
		private IClassFile fClassFile;
		private ISourceRange fSourceRange;
		private ClassFileSourceContext(IClassFile classFile, ISourceRange range) {
			fClassFile= classFile;
			fSourceRange= range;
			if (fSourceRange == null)
				fSourceRange= new SourceRange(0,0);
		}
		public boolean isBinary() {
			return true;
		}
		public ICompilationUnit getCompilationUnit() {
			return null;
		}
		public IClassFile getClassFile() {
			return fClassFile;
		}
		public ISourceRange getSourceRange() {
			return fSourceRange;
		}
		public String toString() {
			return getSourceRange() + " in " + super.toString(); //$NON-NLS-1$
		}
	}
	
	/**
	 * Creates an status entry context for the given member
	 * 
	 * @param member the java member for which the context is supposed 
	 *  to be created
	 * @return the status entry context or <code>null</code> if the
	 * 	context cannot be created
	 */
	public static RefactoringStatusContext create(IMember member) {
		if (member == null || !member.exists())
			return null;
		return new MemberSourceContext(member);
	}
	
	/**
	 * Creates an status entry context for the given import declaration
	 * 
	 * @param declaration the import declaration for which the context is 
	 *  supposed to be created
	 * @return the status entry context or <code>null</code> if the
	 * 	context cannot be created
	 */
	public static RefactoringStatusContext create(IImportDeclaration declaration) {
		if (declaration == null || !declaration.exists())
			return null;
		return new ImportDeclarationSourceContext(declaration);
	}
	
	/**
	 * Creates an status entry context for the given method binding
	 * 
	 * @param method the method binding for which the context is supposed to be created
	 * @return the status entry context or <code>Context.NULL_CONTEXT</code> if the
	 * 	context cannot be created
	 */
	public static RefactoringStatusContext create(IMethodBinding method) {
		return create((IMethod) method.getJavaElement());
	}

	/**
	 * Creates an status entry context for the given compilation unit.
	 * 
	 * @param cunit the compilation unit containing the error
	 * @return the status entry context or <code>Context.NULL_CONTEXT</code> if the
	 * 	context cannot be created
	 */
	public static RefactoringStatusContext create(ICompilationUnit cunit) {
		return create(cunit, (ISourceRange)null);
	}

	/**
	 * Creates an status entry context for the given compilation unit and source range.
	 * 
	 * @param cunit the compilation unit containing the error
	 * @param range the source range that has caused the error or 
	 *  <code>null</code> if the source range is unknown
	 * @return the status entry context or <code>null</code> if the
	 * 	context cannot be created
	 */
	public static RefactoringStatusContext create(ICompilationUnit cunit, ISourceRange range) {
		if (cunit == null)
			return null;
		return new CompilationUnitSourceContext(cunit, range);
	}

	/**
	 * Creates an status entry context for the given compilation unit and AST node.
	 * 
	 * @param cunit the compilation unit containing the error
	 * @param node an astNode denoting the source range that has caused the error
	 * 
	 * @return the status entry context or <code>Context.NULL_CONTEXT</code> if the
	 * 	context cannot be created
	 */
	public static RefactoringStatusContext create(ICompilationUnit cunit, ASTNode node) {
		ISourceRange range= null;
		if (node != null)
			range= new SourceRange(node.getStartPosition(), node.getLength());
		return create(cunit, range);
	}

	/**
	 * Creates an status entry context for the given compilation unit and selection.
	 * 
	 * @param cunit the compilation unit containing the error
	 * @param selection a selection denoting the source range that has caused the error
	 * 
	 * @return the status entry context or <code>Context.NULL_CONTEXT</code> if the
	 * 	context cannot be created
	 */
	public static RefactoringStatusContext create(ICompilationUnit cunit, Selection selection) {
		ISourceRange range= null;
		if (selection != null)
			range= new SourceRange(selection.getOffset(), selection.getLength());
		return create(cunit, range);
	}

	/**
	 * Creates an status entry context for the given class file.
	 * 
	 * @param classFile the class file containing the error
	 * @return the status entry context or <code>Context.NULL_CONTEXT</code> if the
	 *  context cannot be created
	 */
	public static RefactoringStatusContext create(IClassFile classFile) {
		return create(classFile, (ISourceRange)null);
	}

	/**
	 * Creates an status entry context for the given class file and source range.
	 * 
	 * @param classFile the class file containing the error
	 * @param range the source range that has caused the error or 
	 *  <code>null</code> if the source range is unknown
	 * @return the status entry context or <code>null</code> if the
	 *  context cannot be created
	 */
	public static RefactoringStatusContext create(IClassFile classFile, ISourceRange range) {
		if (classFile == null)
			return null;
		return new ClassFileSourceContext(classFile, range);
	}

	/**
	 * Creates an status entry context for the given class file and AST node.
	 * 
	 * @param classFile the class file containing the error
	 * @param node an astNode denoting the source range that has caused the error
	 * 
	 * @return the status entry context or <code>Context.NULL_CONTEXT</code> if the
	 *  context cannot be created
	 */
	public static RefactoringStatusContext create(IClassFile classFile, ASTNode node) {
		ISourceRange range= null;
		if (node != null)
			range= new SourceRange(node.getStartPosition(), node.getLength());
		return create(classFile, range);
	}

	/**
	 * Returns whether this context is for a class file.
	 *
	 * @return <code>true</code> if from a class file, and <code>false</code> if
	 *   from a compilation unit
	 */
	public abstract boolean isBinary();
	
	/**
	 * Returns the compilation unit this context is working on. Returns <code>null</code>
	 * if the context is a binary context.
	 * 
	 * @return the compilation unit
	 */
	public abstract ICompilationUnit getCompilationUnit();
	
	/**
	 * Returns the class file this context is working on. Returns <code>null</code>
	 * if the context is not a binary context.
	 * 
	 * @return the class file
	 */
	public abstract IClassFile getClassFile();
	
	/**
	 * Returns the source range associated with this element.
	 *
	 * @return the source range
	 */
	public abstract ISourceRange getSourceRange();
	
	/* (non-Javadoc)
	 * Method declared on Context.
	 */
	public Object getCorrespondingElement() {
		if (isBinary())
			return getClassFile();
		else
			return getCompilationUnit();
	}	
}

