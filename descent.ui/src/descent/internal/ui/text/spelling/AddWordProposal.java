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

package descent.internal.ui.text.spelling;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.IContextInformation;

import descent.internal.corext.util.Messages;

import descent.ui.PreferenceConstants;
import descent.ui.text.java.IInvocationContext;
import descent.ui.text.java.IJavaCompletionProposal;

import descent.internal.ui.JavaPluginImages;
import descent.internal.ui.JavaUIMessages;
import descent.internal.ui.text.spelling.engine.ISpellCheckEngine;
import descent.internal.ui.text.spelling.engine.ISpellChecker;

/**
 * Proposal to add the unknown word to the dictionaries.
 *
 * @since 3.0
 */
public class AddWordProposal implements IJavaCompletionProposal {

	/** The invocation context */
	private final IInvocationContext fContext;

	/** The word to add */
	private final String fWord;

	/**
	 * Creates a new add word proposal
	 *
	 * @param word
	 *                   The word to add
	 * @param context
	 *                   The invocation context
	 */
	public AddWordProposal(final String word, final IInvocationContext context) {
		fContext= context;
		fWord= word;
	}

	/*
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#apply(org.eclipse.jface.text.IDocument)
	 */
	public final void apply(final IDocument document) {

		final ISpellCheckEngine engine= SpellCheckEngine.getInstance();
		final ISpellChecker checker= engine.createSpellChecker(engine.getLocale(), PreferenceConstants.getPreferenceStore());

		if (checker != null) {
			checker.addWord(fWord);
			JavaSpellingProblem.removeAllInActiveEditor(fWord);
		}
	}

	/*
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getAdditionalProposalInfo()
	 */
	public String getAdditionalProposalInfo() {
		return Messages.format(JavaUIMessages.Spelling_add_info, new String[] { WordCorrectionProposal.getHtmlRepresentation(fWord)});
	}

	/*
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getContextInformation()
	 */
	public final IContextInformation getContextInformation() {
		return null;
	}

	/*
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getDisplayString()
	 */
	public String getDisplayString() {
		return Messages.format(JavaUIMessages.Spelling_add_label, new String[] { fWord });
	}

	/*
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getImage()
	 */
	public Image getImage() {
		return JavaPluginImages.get(JavaPluginImages.IMG_CORRECTION_ADD);
	}

	/*
	 * @see descent.ui.text.java.IJavaCompletionProposal#getRelevance()
	 */
	public int getRelevance() {
		return Integer.MIN_VALUE;
	}

	/*
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getSelection(org.eclipse.jface.text.IDocument)
	 */
	public final Point getSelection(final IDocument document) {
		return new Point(fContext.getSelectionOffset(), fContext.getSelectionLength());
	}
}
