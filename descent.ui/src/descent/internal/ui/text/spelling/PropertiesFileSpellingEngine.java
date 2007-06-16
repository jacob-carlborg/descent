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
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.TypedRegion;

import org.eclipse.ui.texteditor.spelling.ISpellingProblemCollector;

import descent.internal.ui.JavaPlugin;
import descent.internal.ui.propertiesfileeditor.IPropertiesFilePartitions;
import descent.internal.ui.text.spelling.engine.ISpellChecker;
import descent.internal.ui.text.spelling.engine.ISpellEventListener;

/**
 * Properties file spelling engine
 *
 * @since 3.1
 */
public class PropertiesFileSpellingEngine extends SpellingEngine {

	/*
	 * @see descent.internal.ui.text.spelling.newapi.SpellingEngine#check(org.eclipse.jface.text.IDocument, org.eclipse.jface.text.IRegion[], descent.internal.ui.text.spelling.engine.ISpellChecker, java.util.Locale, org.eclipse.ui.texteditor.spelling.ISpellingProblemCollector, org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected void check(IDocument document, IRegion[] regions, ISpellChecker checker, Locale locale, ISpellingProblemCollector collector, IProgressMonitor monitor) {
		ISpellEventListener listener= new SpellEventListener(collector);
		try {
			checker.addListener(listener);
			List partitionList= new ArrayList();
			for (int i= 0; i < regions.length; i++)
				partitionList.addAll(Arrays.asList(TextUtilities.computePartitioning(document, IPropertiesFilePartitions.PROPERTIES_FILE_PARTITIONING, regions[i].getOffset(), regions[i].getLength(), false)));
			ITypedRegion[] partitions= (ITypedRegion[]) partitionList.toArray(new ITypedRegion[partitionList.size()]);

			for (int i= 0; i < partitions.length; i++) {
				ITypedRegion partition= partitions[i];
				if (IPropertiesFilePartitions.COMMENT.equals(partition.getType())) {
					for (; i < partitions.length - 1; i++) {
						ITypedRegion next= partitions[i+1];
						int gapOffset= partition.getOffset() + partition.getLength();
						int gapLength= next.getOffset() - gapOffset;
						if ((IPropertiesFilePartitions.COMMENT.equals(next.getType()) || isWhitespace(document, next.getOffset(), next.getLength())) && isWhitespace(document, gapOffset, gapLength))
							partition= new TypedRegion(partition.getOffset(), next.getOffset() + next.getLength() - partition.getOffset(), partition.getType());
						else
							break;
					}
				}
				if (IPropertiesFilePartitions.COMMENT.equals(partition.getType()) || IPropertiesFilePartitions.PROPERTY_VALUE.equals(partition.getType()))
					checker.execute(new SpellCheckIterator(document, partition, locale, new PropertiesValueBreakIterator(locale)));
			}
		} catch (BadLocationException x) {
			JavaPlugin.log(x);
		} finally {
			checker.removeListener(listener);
		}
	}

	/**
	 * Returns <code>true</code> iff the given region contains only
	 * whitespace.
	 *
	 * @param document the document
	 * @param offset the region's offset
	 * @param length the region's length
	 * @return <code>true</code> iff the given region contains only
	 *         whitespace
	 */
	private boolean isWhitespace(IDocument document, int offset, int length) {
		try {
			for (int i= 0; i < length; i++)
				if (!Character.isWhitespace(document.getChar(offset + i)))
					return false;
			return true;
		} catch (BadLocationException x) {
			JavaPlugin.log(x);
			return false;
		}
	}
}
