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

import org.eclipse.text.edits.TextEdit;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;

import org.eclipse.swt.graphics.Image;

import org.eclipse.jface.preference.IPreferenceStore;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;

import descent.core.ICompilationUnit;
import descent.core.IType;
import descent.core.Signature;
import descent.core.dom.rewrite.ImportRewrite;

import descent.internal.corext.codemanipulation.StubUtility;

import descent.ui.PreferenceConstants;

import descent.internal.ui.JavaPlugin;

/**
 * If passed compilation unit is not null, the replacement string will be seen as a qualified type name.
  */
public class JavaTypeCompletionProposal extends JavaCompletionProposal {

	protected final ICompilationUnit fCompilationUnit;

	/** The unqualified type name. */
	private final String fUnqualifiedTypeName;
	/** The fully qualified type name. */
	private final String fFullyQualifiedTypeName;

	public JavaTypeCompletionProposal(String replacementString, ICompilationUnit cu, int replacementOffset, int replacementLength, Image image, String displayString, int relevance) {
		this(replacementString, cu, replacementOffset, replacementLength, image, displayString, relevance, null);
	}

	public JavaTypeCompletionProposal(String replacementString, ICompilationUnit cu, int replacementOffset, int replacementLength, Image image, String displayString, int relevance,
		String fullyQualifiedTypeName)
	{
		super(replacementString, replacementOffset, replacementLength, image, displayString, relevance);
		fCompilationUnit= cu;
		fFullyQualifiedTypeName= fullyQualifiedTypeName;
		
		// TODO JDT signature
//		fUnqualifiedTypeName= fullyQualifiedTypeName != null ? Signature.getSimpleName(fullyQualifiedTypeName) : null;
		fUnqualifiedTypeName = null;
	}

	protected boolean updateReplacementString(IDocument document, char trigger, int offset, ImportRewrite impRewrite) throws CoreException, BadLocationException {
		// avoid adding imports when inside imports container
		if (impRewrite != null && fFullyQualifiedTypeName != null) {
			String replacementString= getReplacementString();
			String qualifiedType= fFullyQualifiedTypeName;
			if (qualifiedType.indexOf('.') != -1 && replacementString.startsWith(qualifiedType) && !replacementString.endsWith(String.valueOf(';'))) {
				IType[] types= impRewrite.getCompilationUnit().getTypes();
				if (types.length > 0 && types[0].getSourceRange().getOffset() <= offset) {
					// ignore positions above type.
					setReplacementString(impRewrite.addImport(getReplacementString()));
					return true;
				}
			}
		}
		return false;
	}


	/* (non-Javadoc)
	 * @see ICompletionProposalExtension#apply(IDocument, char, int)
	 */
	public void apply(IDocument document, char trigger, int offset) {
		try {
			ImportRewrite impRewrite= null;

			if (fCompilationUnit != null && allowAddingImports()) {
				impRewrite= StubUtility.createImportRewrite(fCompilationUnit, true);
			}

			boolean importAdded= updateReplacementString(document, trigger, offset, impRewrite);

			if (importAdded)
				setCursorPosition(getReplacementString().length());

			super.apply(document, trigger, offset);

			if (importAdded && impRewrite != null) {
				int oldLen= document.getLength();
				impRewrite.rewriteImports(new NullProgressMonitor()).apply(document, TextEdit.UPDATE_REGIONS);
				setReplacementOffset(getReplacementOffset() + document.getLength() - oldLen);
			}
		} catch (CoreException e) {
			JavaPlugin.log(e);
		} catch (BadLocationException e) {
			JavaPlugin.log(e);
		}
	}

	private boolean allowAddingImports() {
		IPreferenceStore preferenceStore= JavaPlugin.getDefault().getPreferenceStore();
		return preferenceStore.getBoolean(PreferenceConstants.CODEASSIST_ADDIMPORT);
	}

	/*
	 * @see descent.internal.ui.text.java.AbstractJavaCompletionProposal#isValidPrefix(java.lang.String)
	 */
	protected boolean isValidPrefix(String prefix) {
		return super.isValidPrefix(prefix) || isPrefix(prefix, fUnqualifiedTypeName) || isPrefix(prefix, fFullyQualifiedTypeName);
	}

	/*
	 * @see descent.internal.ui.text.java.JavaCompletionProposal#getCompletionText()
	 */
	public CharSequence getPrefixCompletionText(IDocument document, int completionOffset) {
		return fUnqualifiedTypeName;
	}

}
