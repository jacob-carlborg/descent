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

import java.util.Locale;

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.TextUtilities;

import org.eclipse.ui.texteditor.spelling.ISpellingProblemCollector;

import descent.ui.text.IJavaPartitions;

import descent.internal.ui.JavaPlugin;
import descent.internal.ui.text.spelling.engine.ISpellChecker;
import descent.internal.ui.text.spelling.engine.ISpellEventListener;

/**
 * Java spelling engine
 *
 * @since 3.1
 */
public class JavaSpellingEngine extends SpellingEngine {

	/*
	 * @see descent.internal.ui.text.spelling.newapi.SpellingEngine#check(org.eclipse.jface.text.IDocument, org.eclipse.jface.text.IRegion[], descent.internal.ui.text.spelling.engine.ISpellChecker, java.util.Locale, org.eclipse.ui.texteditor.spelling.ISpellingProblemCollector, org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected void check(IDocument document, IRegion[] regions, ISpellChecker checker, Locale locale, ISpellingProblemCollector collector, IProgressMonitor monitor) {
		ISpellEventListener listener= new SpellEventListener(collector);
		try {
			checker.addListener(listener);
			try {
				for (int i= 0; i < regions.length; i++) {
					IRegion region= regions[i];
					ITypedRegion[] partitions= TextUtilities.computePartitioning(document, IJavaPartitions.JAVA_PARTITIONING, region.getOffset(), region.getLength(), false);
					for (int index= 0; index < partitions.length; index++) {
						if (monitor != null && monitor.isCanceled())
							return;

						ITypedRegion partition= partitions[index];
						if (!partition.getType().equals(IDocument.DEFAULT_CONTENT_TYPE))
							checker.execute(new SpellCheckIterator(document, partition, locale));
					}
				}
			} catch (BadLocationException x) {
				JavaPlugin.log(x);
			}
		} finally {
			checker.removeListener(listener);
		}
	}
}
