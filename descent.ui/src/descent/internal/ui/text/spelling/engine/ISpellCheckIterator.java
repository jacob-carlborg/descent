package descent.internal.ui.text.spelling.engine;

import java.util.Iterator;

/**
 * Interface for iterators used for spell-checking.
 *
 * @since 3.0
 */
public interface ISpellCheckIterator extends Iterator {

	/**
	 * Returns the begin index (inclusive) of the current word.
	 *
	 * @return The begin index of the current word
	 */
	public int getBegin();

	/**
	 * Returns the end index (exclusive) of the current word.
	 *
	 * @return The end index of the current word
	 */
	public int getEnd();

	/**
	 * Does the current word start a new sentence?
	 *
	 * @return <code>true<code> iff the current word starts a new sentence, <code>false</code> otherwise
	 */
	public boolean startsSentence();
}
