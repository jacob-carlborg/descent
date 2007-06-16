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

package descent.internal.ui.text.spelling.engine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import descent.internal.ui.JavaPlugin;

/**
 * Partial implementation of a spell dictionary.
 *
 * @since 3.0
 */
public abstract class AbstractSpellDictionary implements ISpellDictionary {

	/** The bucket capacity */
	protected static final int BUCKET_CAPACITY= 4;

	/** The word buffer capacity */
	protected static final int BUFFER_CAPACITY= 32;

	/** The distance threshold */
	protected static final int DISTANCE_THRESHOLD= 160;

	/** The hash capacity */
	protected static final int HASH_CAPACITY= 22 * 1024;

	/** The phonetic distance algorithm */
	private IPhoneticDistanceAlgorithm fDistanceAlgorithm= new DefaultPhoneticDistanceAlgorithm();

	/** The mapping from phonetic hashes to word lists */
	private final Map fHashBuckets= new HashMap(HASH_CAPACITY);

	/** The phonetic hash provider */
	private IPhoneticHashProvider fHashProvider= new DefaultPhoneticHashProvider();

	/** Is the dictionary already loaded? */
	private boolean fLoaded= false;
	/**
	 * Must the dictionary be loaded?
	 * @since 3.2
	 */
	private boolean fMustLoad= true;

	/**
	 * Returns all candidates with the same phonetic hash.
	 *
	 * @param hash
	 *                   The hash to retrieve the candidates of
	 * @return Array of candidates for the phonetic hash
	 */
	protected final ArrayList getCandidates(final String hash) {

		ArrayList list= (ArrayList)fHashBuckets.get(hash);
		if (list == null)
			list= new ArrayList(0);

		return list;
	}

	/**
	 * Returns all candidates that have a phonetic hash within a bounded
	 * distance to the specified word.
	 *
	 * @param word
	 *                   The word to find the nearest matches for
	 * @param sentence
	 *                   <code>true</code> iff the proposals start a new sentence,
	 *                   <code>false</code> otherwise
	 * @param hashs
	 *                   Array of close hashes to find the matches
	 * @return Set of ranked words with bounded distance to the specified word
	 */
	protected final HashSet getCandidates(final String word, final boolean sentence, final ArrayList hashs) {

		int distance= 0;
		String hash= null;

		String candidate= null;
		List candidates= null;

		final StringBuffer buffer= new StringBuffer(BUFFER_CAPACITY);
		final HashSet result= new HashSet(BUCKET_CAPACITY * hashs.size());

		for (int index= 0; index < hashs.size(); index++) {

			hash= (String)hashs.get(index);
			candidates= getCandidates(hash);

			for (int offset= 0; offset < candidates.size(); offset++) {

				candidate= (String)candidates.get(offset);
				distance= fDistanceAlgorithm.getDistance(word, candidate);

				if (distance < DISTANCE_THRESHOLD) {

					buffer.setLength(0);
					buffer.append(candidate);

					if (sentence)
						buffer.setCharAt(0, Character.toUpperCase(buffer.charAt(0)));

					result.add(new RankedWordProposal(buffer.toString(), -distance));
				}
			}
		}
		return result;
	}

	/**
	 * Returns all approximations that have a phonetic hash with smallest
	 * possible distance to the specified word.
	 *
	 * @param word
	 *                   The word to find the nearest matches for
	 * @param sentence
	 *                   <code>true</code> iff the proposals start a new sentence,
	 *                   <code>false</code> otherwise
	 * @param result
	 *                   Set of ranked words with smallest possible distance to the
	 *                   specified word
	 */
	protected final void getCandidates(final String word, final boolean sentence, final HashSet result) {

		int distance= 0;
		int minimum= Integer.MAX_VALUE;

		String candidate= null;
		StringBuffer buffer= new StringBuffer(BUFFER_CAPACITY);

		final ArrayList candidates= getCandidates(fHashProvider.getHash(word));
		final ArrayList matches= new ArrayList(candidates.size());

		for (int index= 0; index < candidates.size(); index++) {

			candidate= (String)candidates.get(index);
			distance= fDistanceAlgorithm.getDistance(word, candidate);

			if (distance <= minimum) {

				buffer.setLength(0);
				buffer.append(candidate);

				if (sentence)
					buffer.setCharAt(0, Character.toUpperCase(buffer.charAt(0)));

				matches.add(new RankedWordProposal(buffer.toString(), -distance));
				minimum= distance;
			}
		}

		RankedWordProposal match= null;

		for (int index= 0; index < matches.size(); index++) {

			match= (RankedWordProposal)matches.get(index);
			if (match.getRank() == minimum)
				result.add(match);
		}
	}

	/**
	 * Returns the used phonetic distance algorithm.
	 *
	 * @return The phonetic distance algorithm
	 */
	protected final IPhoneticDistanceAlgorithm getDistanceAlgorithm() {
		return fDistanceAlgorithm;
	}

	/**
	 * Returns the used phonetic hash provider.
	 *
	 * @return The phonetic hash provider
	 */
	protected final IPhoneticHashProvider getHashProvider() {
		return fHashProvider;
	}

	/*
	 * @see descent.internal.ui.text.spelling.engine.ISpellDictionary#getProposals(java.lang.String,boolean)
	 */
	public Set getProposals(final String word, final boolean sentence) {

		try {

			if (fMustLoad)
				load(getURL());

		} catch (MalformedURLException exception) {
			// Do nothing
		}

		final String hash= fHashProvider.getHash(word);
		final char[] mutators= fHashProvider.getMutators();

		final ArrayList neighborhood= new ArrayList((word.length() + 1) * (mutators.length + 2));
		neighborhood.add(hash);

		final HashSet candidates= getCandidates(word, sentence, neighborhood);
		neighborhood.clear();

		char previous= 0;
		char next= 0;

		char[] characters= word.toCharArray();
		for (int index= 0; index < word.length() - 1; index++) {

			next= characters[index];
			previous= characters[index + 1];

			characters[index]= previous;
			characters[index + 1]= next;

			neighborhood.add(fHashProvider.getHash(new String(characters)));

			characters[index]= next;
			characters[index + 1]= previous;
		}

		final String sentinel= word + " "; //$NON-NLS-1$

		characters= sentinel.toCharArray();
		int offset= characters.length - 1;

		while (true) {

			for (int index= 0; index < mutators.length; index++) {

				characters[offset]= mutators[index];
				neighborhood.add(fHashProvider.getHash(new String(characters)));
			}

			if (offset == 0)
				break;

			characters[offset]= characters[offset - 1];
			--offset;
		}

		char mutated= 0;
		characters= word.toCharArray();

		for (int index= 0; index < word.length(); index++) {

			mutated= characters[index];
			for (int mutator= 0; mutator < mutators.length; mutator++) {

				characters[index]= mutators[mutator];
				neighborhood.add(fHashProvider.getHash(new String(characters)));
			}
			characters[index]= mutated;
		}

		characters= word.toCharArray();
		final char[] deleted= new char[characters.length - 1];

		for (int index= 0; index < deleted.length; index++)
			deleted[index]= characters[index];

		next= characters[characters.length - 1];
		offset= deleted.length;

		while (true) {

			neighborhood.add(fHashProvider.getHash(new String(characters)));
			if (offset == 0)
				break;

			previous= next;
			next= deleted[offset - 1];

			deleted[offset - 1]= previous;
			--offset;
		}

		neighborhood.remove(hash);
		final HashSet matches= getCandidates(word, sentence, neighborhood);

		if (matches.size() == 0 && candidates.size() == 0)
			getCandidates(word, sentence, candidates);

		candidates.addAll(matches);

		return candidates;
	}

	/**
	 * Returns the URL of the dictionary word list.
	 *
	 * @throws MalformedURLException
	 *                    if the URL could not be retrieved
	 * @return The URL of the dictionary word list
	 */
	protected abstract URL getURL() throws MalformedURLException;

	/**
	 * Hashes the word into the dictionary.
	 *
	 * @param word
	 *                   The word to hash in the dictionary
	 */
	protected final void hashWord(final String word) {

		final String hash= fHashProvider.getHash(word);
		ArrayList bucket= (ArrayList)fHashBuckets.get(hash);

		if (bucket == null) {

			bucket= new ArrayList(BUCKET_CAPACITY);
			fHashBuckets.put(hash, bucket);
		}

		bucket.add(word);
	}

	/*
	 * @see descent.internal.ui.text.spelling.engine.ISpellDictionary#isCorrect(java.lang.String)
	 */
	public boolean isCorrect(final String word) {

		try {

			if (fMustLoad)
				load(getURL());

		} catch (MalformedURLException exception) {
			// Do nothing
		}

		final ArrayList candidates= getCandidates(fHashProvider.getHash(word));

		if (candidates.contains(word) || candidates.contains(word.toLowerCase()))
			return true;

		return false;
	}

	/*
	 * @see descent.ui.text.spelling.engine.ISpellDictionary#isLoaded()
	 */
	public final synchronized boolean isLoaded() {
		return fLoaded || fHashBuckets.size() > 0;
	}

	/**
	 * Loads a dictionary word list from disk.
	 *
	 * @param url
	 *                   The URL of the word list to load
	 * @return <code>true</code> iff the word list could be loaded, <code>false</code>
	 *               otherwise
	 */
	protected synchronized boolean load(final URL url) {

		if (url != null) {

			InputStream stream= null;
			try {

				stream= url.openStream();
				if (stream != null) {

					String word= null;

					final BufferedReader reader= new BufferedReader(new InputStreamReader(stream));
					while ((word= reader.readLine()) != null)
						hashWord(word);

					return fLoaded= true;
				}
			} catch (IOException exception) {
				JavaPlugin.log(exception);
			} finally {
				fMustLoad= false;
				try {
					if (stream != null)
						stream.close();
				} catch (IOException x) {
				}
			}
		}
		return false;
	}

	/**
	 * Sets the phonetic distance algorithm to use.
	 *
	 * @param algorithm
	 *                   The phonetic distance algorithm
	 */
	protected final void setDistanceAlgorithm(final IPhoneticDistanceAlgorithm algorithm) {
		fDistanceAlgorithm= algorithm;
	}

	/**
	 * Sets the phonetic hash provider to use.
	 *
	 * @param provider
	 *                   The phonetic hash provider
	 */
	protected final void setHashProvider(final IPhoneticHashProvider provider) {
		fHashProvider= provider;
	}

	/*
	 * @see descent.ui.text.spelling.engine.ISpellDictionary#unload()
	 */
	public synchronized void unload() {

		fLoaded= false;
		fHashBuckets.clear();
	}

	/*
	 * @see descent.ui.text.spelling.engine.ISpellDictionary#acceptsWords()
	 */
	public boolean acceptsWords() {
		return false;
	}

	/*
	 * @see descent.internal.ui.text.spelling.engine.ISpellDictionary#addWord(java.lang.String)
	 */
	public void addWord(final String word) {
		// Do nothing
	}
}
