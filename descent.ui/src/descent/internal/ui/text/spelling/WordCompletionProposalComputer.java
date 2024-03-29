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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;


import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

import descent.ui.PreferenceConstants;
import descent.ui.text.java.IJavaCompletionProposalComputer;
import descent.ui.text.java.ContentAssistInvocationContext;

import descent.internal.ui.JavaPlugin;
import descent.internal.ui.JavaPluginImages;
import descent.internal.ui.text.java.JavaCompletionProposal;
import descent.internal.ui.text.spelling.engine.ISpellCheckEngine;
import descent.internal.ui.text.spelling.engine.ISpellChecker;
import descent.internal.ui.text.spelling.engine.RankedWordProposal;

/**
 * Content assist processor to complete words.
 *
 * @since 3.0
 */
public final class WordCompletionProposalComputer implements IJavaCompletionProposalComputer {

	/** The prefix rank shift */
	private static final int PREFIX_RANK_SHIFT= 4096;

	/*
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposalComputer#computeCompletionProposals(org.eclipse.jface.text.contentassist.TextContentAssistInvocationContext, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public List computeCompletionProposals(ContentAssistInvocationContext context, IProgressMonitor monitor) {
		if (contributes()) {
			try {
				IDocument document= context.getDocument();
				final int offset= context.getInvocationOffset();
			
				final IRegion region= document.getLineInformationOfOffset(offset);
				final String content= document.get(region.getOffset(), region.getLength());
			
				int index= offset - region.getOffset() - 1;
				while (index >= 0 && Character.isLetter(content.charAt(index)))
					index--;
			
				final int start= region.getOffset() + index + 1;
				final String candidate= content.substring(index + 1, offset - region.getOffset());
			
				if (candidate.length() > 0) {
			
					final ISpellCheckEngine engine= SpellCheckEngine.getInstance();
					final ISpellChecker checker= engine.createSpellChecker(engine.getLocale(), PreferenceConstants.getPreferenceStore());
			
					if (checker != null) {
			
						final List proposals= new ArrayList(checker.getProposals(candidate, Character.isUpperCase(candidate.charAt(0))));
						final List result= new ArrayList(proposals.size());
			
						for (Iterator it= proposals.iterator(); it.hasNext();) {
							RankedWordProposal word= (RankedWordProposal) it.next();
							String text= word.getText();
							if (text.startsWith(candidate))
								word.setRank(word.getRank() + PREFIX_RANK_SHIFT);
							
							result.add(new JavaCompletionProposal(text, start, candidate.length(), JavaPluginImages.get(JavaPluginImages.IMG_CORRECTION_RENAME), text, word.getRank()) {
								/*
								 * @see descent.internal.ui.text.java.JavaCompletionProposal#validate(org.eclipse.jface.text.IDocument, int, org.eclipse.jface.text.DocumentEvent)
								 */
								public boolean validate(IDocument doc, int validate_offset, DocumentEvent event) {
									return offset == validate_offset;
								}
							});
						}
						
						return result;
					}
				}
			} catch (BadLocationException exception) {
				// log & ignore
				JavaPlugin.log(exception);
			}
		}
		return Collections.EMPTY_LIST;
	}

	private boolean contributes() {
		return PreferenceConstants.getPreferenceStore().getBoolean(PreferenceConstants.SPELLING_ENABLE_CONTENTASSIST);
	}

	/*
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposalComputer#computeContextInformation(org.eclipse.jface.text.contentassist.TextContentAssistInvocationContext, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public List computeContextInformation(ContentAssistInvocationContext context, IProgressMonitor monitor) {
		return Collections.EMPTY_LIST;
	}

	/*
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposalComputer#getErrorMessage()
	 */
	public String getErrorMessage() {
		return null; // no error message available
	}

	/*
	 * @see descent.ui.text.java.IJavaCompletionProposalComputer#sessionStarted()
	 */
	public void sessionStarted() {
	}

	/*
	 * @see descent.ui.text.java.IJavaCompletionProposalComputer#sessionEnded()
	 */
	public void sessionEnded() {
	}
}
