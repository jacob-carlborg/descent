/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
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
import java.util.List;

import org.eclipse.core.runtime.CoreException;

import descent.core.ICompilationUnit;
import descent.internal.ui.text.javadoc.IHtmlTagConstants;
import descent.internal.ui.text.javadoc.IJavaDocTagConstants;
import descent.internal.ui.text.spelling.engine.ISpellCheckEngine;
import descent.internal.ui.text.spelling.engine.ISpellChecker;
import descent.internal.ui.text.spelling.engine.RankedWordProposal;
import descent.ui.PreferenceConstants;
import descent.ui.text.java.IInvocationContext;
import descent.ui.text.java.IJavaCompletionProposal;
import descent.ui.text.java.IProblemLocation;
import descent.ui.text.java.IQuickFixProcessor;

/**
 * Quick fix processor for incorrectly spelled words.
 *
 * @since 3.0
 */
public class WordQuickFixProcessor implements IQuickFixProcessor, IHtmlTagConstants, IJavaDocTagConstants {

	/**
	 * Creates a new word quick fix processor.
	 */
	public WordQuickFixProcessor() {
		// For extension point
	}

	/*
	 * @see descent.ui.text.java.IQuickFixProcessor#getCorrections(descent.ui.text.java.IInvocationContext,descent.ui.text.java.IProblemLocation[])
	 */
	public IJavaCompletionProposal[] getCorrections(IInvocationContext context, IProblemLocation[] locations) throws CoreException {

		final int threshold= PreferenceConstants.getPreferenceStore().getInt(PreferenceConstants.SPELLING_PROPOSAL_THRESHOLD);

		int size= 0;
		List proposals= null;
		String[] arguments= null;

		IProblemLocation location= null;
		RankedWordProposal proposal= null;
		IJavaCompletionProposal[] result= null;

		boolean fixed= false;
		boolean match= false;
		boolean sentence= false;

		final ISpellCheckEngine engine= SpellCheckEngine.getInstance();
		final ISpellChecker checker= engine.createSpellChecker(engine.getLocale(), PreferenceConstants.getPreferenceStore());

		if (checker != null) {

			for (int index= 0; index < locations.length; index++) {

				location= locations[index];
				if (location.getProblemId() == JavaSpellingReconcileStrategy.SPELLING_PROBLEM_ID) {

					arguments= location.getProblemArguments();
					if (arguments != null && arguments.length > 4) {

						sentence= Boolean.valueOf(arguments[3]).booleanValue();
						match= Boolean.valueOf(arguments[4]).booleanValue();
						fixed= arguments[0].charAt(0) == HTML_TAG_PREFIX || arguments[0].charAt(0) == JAVADOC_TAG_PREFIX;

						if ((sentence && match) && !fixed)
							result= new IJavaCompletionProposal[] { new ChangeCaseProposal(arguments, location.getOffset(), location.getLength(), context, engine.getLocale())};
						else {

							proposals= new ArrayList(checker.getProposals(arguments[0], sentence));
							size= proposals.size();

							if (threshold > 0 && size > threshold) {

								Collections.sort(proposals);
								proposals= proposals.subList(size - threshold - 1, size - 1);
								size= proposals.size();
							}

							boolean extendable= !fixed ? checker.acceptsWords() : false;
							result= new IJavaCompletionProposal[size + (extendable ? 3 : 2)];

							for (index= 0; index < size; index++) {

								proposal= (RankedWordProposal)proposals.get(index);
								result[index]= new WordCorrectionProposal(proposal.getText(), arguments, location.getOffset(), location.getLength(), context, proposal.getRank());
							}

							if (extendable)
								result[index++]= new AddWordProposal(arguments[0], context);

							result[index++]= new WordIgnoreProposal(arguments[0], context);
							result[index++]= new DisableSpellCheckingProposal(context);
						}
						break;
					}
				}
			}
		}
		return result;
	}

	/*
	 * @see descent.ui.text.java.IQuickFixProcessor#hasCorrections(descent.core.ICompilationUnit,int)
	 */
	public final boolean hasCorrections(ICompilationUnit unit, int id) {
		return id == JavaSpellingReconcileStrategy.SPELLING_PROBLEM_ID;
	}
}
