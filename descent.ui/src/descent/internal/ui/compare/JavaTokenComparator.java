package descent.internal.ui.compare;

import org.eclipse.jface.util.Assert;

import descent.core.ToolFactory;
import descent.core.compiler.*;

import org.eclipse.compare.contentmergeviewer.ITokenComparator;
import org.eclipse.compare.rangedifferencer.IRangeComparator;


/**
 * A comparator for Java tokens.
 */
public class JavaTokenComparator implements ITokenComparator {
		
	private String fText;
	private boolean fShouldEscape= true;
	private int fCount;
	private int[] fStarts;
	private int[] fLengths;

	/**
	 * Creates a TokenComparator for the given string.
	 */
	public JavaTokenComparator(String text, boolean shouldEscape) {
		
		Assert.isNotNull(text);
		
		fText= text;
		fShouldEscape= shouldEscape;
		
		int length= fText.length();
		fStarts= new int[length];
		fLengths= new int[length];
		fCount= 0;
		
		IScanner scanner= ToolFactory.createScanner(true, true, false, false); // returns comments & whitespace
		scanner.setSource(fText.toCharArray());
		try {
			int endPos= 0;
			while (scanner.getNextToken() != ITerminalSymbols.TokenNameEOF) {
				int start= scanner.getCurrentTokenStartPosition();
				int end= scanner.getCurrentTokenEndPosition()+1;
				fStarts[fCount]= start;
				fLengths[fCount]= end - start;
				endPos= end;
				fCount++;
			}
			// workaround for #13907
			if (endPos < length) {
				fStarts[fCount]= endPos;
				fLengths[fCount]= length-endPos;
				fCount++;
			}
		} catch (InvalidInputException ex) {
			// NeedWork
		}
	}	

	/**
	 * Returns the number of token in the string.
	 *
	 * @return number of token in the string
	 */
	public int getRangeCount() {
		return fCount;
	}

	/* (non Javadoc)
	 * see ITokenComparator.getTokenStart
	 */
	public int getTokenStart(int index) {
		if (index >= 0 && index < fCount)
			return fStarts[index];
		if (fCount > 0)
			return fStarts[fCount-1] + fLengths[fCount-1];
		return 0;
	}

	/* (non Javadoc)
	 * see ITokenComparator.getTokenLength
	 */
	public int getTokenLength(int index) {
		if (index < fCount)
			return fLengths[index];
		return 0;
	}
	
	/**
	 * Returns <code>true</code> if a token given by the first index
	 * matches a token specified by the other <code>IRangeComparator</code> and index.
	 *
	 * @param thisIndex	the number of the token within this range comparator
	 * @param other the range comparator to compare this with
	 * @param otherIndex the number of the token within the other comparator
	 * @return <code>true</code> if the token are equal
	 */
	public boolean rangesEqual(int thisIndex, IRangeComparator other, int otherIndex) {
		if (other != null && getClass() == other.getClass()) {
			JavaTokenComparator tc= (JavaTokenComparator) other;	// safe cast
			int thisLen= getTokenLength(thisIndex);
			int otherLen= tc.getTokenLength(otherIndex);
			if (thisLen == otherLen)
				return fText.regionMatches(false, getTokenStart(thisIndex), tc.fText, tc.getTokenStart(otherIndex), thisLen);
		}
		return false;
	}

	/**
	 * Aborts the comparison if the number of tokens is too large.
	 *
	 * @return <code>true</code> to abort a token comparison
	 */
	public boolean skipRangeComparison(int length, int max, IRangeComparator other) {

		if (!fShouldEscape)
			return false;

		if (getRangeCount() < 50 || other.getRangeCount() < 50)
			return false;

		if (max < 100)
			return false;

		if (length < 100)
			return false;

		if (max > 800)
			return true;

		if (length < max / 4)
			return false;

		return true;
	}
}
