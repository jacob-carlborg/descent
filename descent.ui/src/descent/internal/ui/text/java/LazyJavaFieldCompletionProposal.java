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
package descent.internal.ui.text.java;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.TextEdit;

import descent.core.CompletionProposal;
import descent.core.ICompilationUnit;
import descent.core.IJavaProject;
import descent.core.JavaCore;
import descent.core.Signature;
import descent.core.dom.CompilationUnit;
import descent.core.dom.rewrite.ImportRewrite;
import descent.internal.corext.codemanipulation.ContextSensitiveImportRewriteContext;
import descent.internal.corext.codemanipulation.StubUtility;
import descent.internal.ui.JavaPlugin;
import descent.internal.ui.javaeditor.ASTProvider;
import descent.ui.PreferenceConstants;
import descent.ui.text.java.JavaContentAssistInvocationContext;

/**
 * If passed compilation unit is not null, the replacement string will be seen as a qualified type name.
  */
public class LazyJavaFieldCompletionProposal extends LazyJavaCompletionProposal {
	/** Triggers for types. Do not modify. */
	protected static final char[] TYPE_TRIGGERS= new char[] { '.', '\t', '[', '(', ' ' };
	/** Triggers for types in javadoc. Do not modify. */
	protected static final char[] JDOC_TYPE_TRIGGERS= new char[] { '#', '}', ' ', '.' };

	/** The compilation unit, or <code>null</code> if none is available. */
	protected final ICompilationUnit fCompilationUnit;

	private String fQualifiedName;
	private String fSimpleName;
	private ImportRewrite fImportRewrite;
	private ContextSensitiveImportRewriteContext fImportContext;

	public LazyJavaFieldCompletionProposal(CompletionProposal proposal, JavaContentAssistInvocationContext context) {
		super(proposal, context);
		fCompilationUnit= context.getCompilationUnit();
		fQualifiedName= null;
	}
	
	public final String getQualifiedTypeName() {
		if (fQualifiedName == null) {
			if (fProposal.getSignature() == null) {
				fQualifiedName = new String(fProposal.getName());
			} else {
				fQualifiedName= String.valueOf(Signature.toCharArray(fProposal.getSignature()));
			}
		}
		return fQualifiedName;
	}
	
	protected final String getSimpleTypeName() {
		if (fSimpleName == null)
			fSimpleName= Signature.getSimpleName(getQualifiedTypeName());
		return fSimpleName;
	}

	/*
	 * @see descent.internal.ui.text.java.LazyJavaCompletionProposal#computeReplacementString()
	 */
	protected String computeReplacementString() {
		String replacement= super.computeReplacementString();

		/* No import rewriting ever from within the import section. */
		if (isImportCompletion())
	        return replacement;
		
		/* Always use the simple name for non-formal javadoc references to types. */
		// TODO fix
		 if (fProposal.getKind() == CompletionProposal.FIELD_REF &&  fInvocationContext.getCoreContext().isInJavadocText())
			 return getSimpleTypeName();
		
		String qualifiedTypeName= getQualifiedTypeName();
 		if (qualifiedTypeName.indexOf('.') == -1)
 			// default package - no imports needed 
 			return qualifiedTypeName;

 		/*
		 * If the user types in the qualification, don't force import rewriting on him - insert the
		 * qualified name.
		 */
 		IDocument document= fInvocationContext.getDocument();
		if (document != null) {
			String prefix= getPrefix(document, getReplacementOffset() + getReplacementLength());
			int dotIndex= prefix.lastIndexOf('.');
			// match up to the last dot in order to make higher level matching still work (camel case...)
			if (dotIndex != -1 && qualifiedTypeName.toLowerCase().startsWith(prefix.substring(0, dotIndex + 1).toLowerCase()))
				return qualifiedTypeName;
		}
		
		/*
		 * The replacement does not contain a qualification (e.g. an inner type qualified by its
		 * parent) - use the replacement directly.
		 */
		if (replacement.indexOf('.') == -1) {
			if (isInJavadoc())
				return getSimpleTypeName(); // don't use the braces added for javadoc link proposals
			return replacement;
		}
		
		/* Add imports if the preference is on. */
		fImportRewrite= createImportRewrite();
		if (fImportRewrite != null) {
			return fImportRewrite.addImport(qualifiedTypeName, fImportContext);
		}
		
		// fall back for the case we don't have an import rewrite (see allowAddingImports)
		
		/* No imports for implicit imports. */
		// TODO JDT signature
//		if (fCompilationUnit != null && JavaModelUtil.isImplicitImport(Signature.getQualifier(qualifiedTypeName), fCompilationUnit)) {
//			return Signature.getSimpleName(qualifiedTypeName);
//		}
		
		/* Default: use the fully qualified type name. */
		return qualifiedTypeName;
	}

	protected final boolean isImportCompletion() {
		char[] completion= fProposal.getCompletion();
		if (completion.length == 0)
			return false;
		
		char last= completion[completion.length - 1];
		/*
		 * Proposals end in a semicolon when completing types in normal imports or when completing
		 * static members, in a period when completing types in static imports.
		 */
		return last == ';' || last == '.';
	}

	private ImportRewrite createImportRewrite() {
		if (fCompilationUnit != null && allowAddingImports()) {
			try {
				CompilationUnit cu= getASTRoot(fCompilationUnit);
				if (cu == null) {
					ImportRewrite rewrite= StubUtility.createImportRewrite(fCompilationUnit, true);
					fImportContext= null;
					return rewrite;
				} else {
					ImportRewrite rewrite= StubUtility.createImportRewrite(cu, true);
					fImportContext= new ContextSensitiveImportRewriteContext(cu, fInvocationContext.getInvocationOffset(), rewrite);
					return rewrite;
				}
			} catch (CoreException x) {
				JavaPlugin.log(x);
			}
		}
		return null;
	}

	private CompilationUnit getASTRoot(ICompilationUnit compilationUnit) {
		return JavaPlugin.getDefault().getASTProvider().getAST(compilationUnit, ASTProvider.WAIT_NO, new NullProgressMonitor());
	}

	/*
	 * @see descent.internal.ui.text.java.LazyJavaCompletionProposal#apply(org.eclipse.jface.text.IDocument, char, int)
	 */
	public void apply(IDocument document, char trigger, int offset) {
		try {
			super.apply(document, trigger, offset);

			if (fImportRewrite != null && fImportRewrite.hasRecordedChanges()) {
				int oldLen= document.getLength();
				fImportRewrite.rewriteImports(new NullProgressMonitor()).apply(document, TextEdit.UPDATE_REGIONS);
				setReplacementOffset(getReplacementOffset() + document.getLength() - oldLen);
			}
		} catch (CoreException e) {
			JavaPlugin.log(e);
		} catch (BadLocationException e) {
			JavaPlugin.log(e);
		}
	}

	/**
	 * Returns <code>true</code> if imports may be added. The return value depends on the context
	 * and preferences only and does not take into account the contents of the compilation unit or
	 * the kind of proposal. Even if <code>true</code> is returned, there may be cases where no
	 * imports are added for the proposal. For example:
	 * <ul>
	 * <li>when completing within the import section</li>
	 * <li>when completing informal javadoc references (e.g. within <code>&lt;code&gt;</code>
	 * tags)</li>
	 * <li>when completing a type that conflicts with an existing import</li>
	 * <li>when completing an implicitly imported type (same package, <code>java.lang</code>
	 * types)</li>
	 * </ul>
	 * <p>
	 * The decision whether a qualified type or the simple type name should be inserted must take
	 * into account these different scenarios.
	 * </p>
	 * <p>
	 * Subclasses may extend.
	 * </p>
	 * 
	 * @return <code>true</code> if imports may be added, <code>false</code> if not
	 */
	protected boolean allowAddingImports() {
		if (isInJavadoc()) {
			// TODO fix
//			if (!fContext.isInJavadocFormalReference())
//				return false;
			if (fProposal.getKind() == CompletionProposal.TYPE_REF &&  fInvocationContext.getCoreContext().isInJavadocText())
				return false;
			
			if (!isJavadocProcessingEnabled())
				return false;
		}
		
		IPreferenceStore preferenceStore= JavaPlugin.getDefault().getPreferenceStore();
		return preferenceStore.getBoolean(PreferenceConstants.CODEASSIST_ADDIMPORT);
	}

	private boolean isJavadocProcessingEnabled() {
		IJavaProject project= fCompilationUnit.getJavaProject();
		boolean processJavadoc;
		if (project == null)
			processJavadoc= JavaCore.ENABLED.equals(JavaCore.getOption(JavaCore.COMPILER_DOC_COMMENT_SUPPORT));
		else
			processJavadoc= JavaCore.ENABLED.equals(project.getOption(JavaCore.COMPILER_DOC_COMMENT_SUPPORT, true));
		return processJavadoc;
	}

	/*
	 * @see descent.internal.ui.text.java.LazyJavaCompletionProposal#isValidPrefix(java.lang.String)
	 */
	protected boolean isValidPrefix(String prefix) {
		return isPrefix(prefix, getSimpleTypeName()) || isPrefix(prefix, getQualifiedTypeName());
	}

	/*
	 * @see descent.internal.ui.text.java.JavaCompletionProposal#getCompletionText()
	 */
	public CharSequence getPrefixCompletionText(IDocument document, int completionOffset) {
		String prefix= getPrefix(document, completionOffset);
		
		String completion;
		// return the qualified name if the prefix is already qualified
		if (prefix.indexOf('.') != -1)
			completion= getQualifiedTypeName();
		else
			completion= getSimpleTypeName();
		
		if (isCamelCaseMatching())
			return getCamelCaseCompound(prefix, completion);

		return completion;
	}
	
	/*
	 * @see descent.internal.ui.text.java.LazyJavaCompletionProposal#computeProposalInfo()
	 */
	protected ProposalInfo computeProposalInfo() {
		if (fCompilationUnit != null) {
			IJavaProject project= fCompilationUnit.getJavaProject();
			if (project != null)
				return new FieldProposalInfo(project, fProposal);
		}
		return super.computeProposalInfo();
	}
}
