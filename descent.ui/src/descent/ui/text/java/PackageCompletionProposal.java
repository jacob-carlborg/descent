package descent.ui.text.java;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;

import descent.internal.ui.text.java.JavaCompletionProposal;

public class PackageCompletionProposal extends JavaCompletionProposal {

	public PackageCompletionProposal(String replacementString, int replacementOffset, int replacementLength, Image image, String displayString, int relevance) {
		super(replacementString, replacementOffset, replacementLength, image,
				displayString, relevance);
	}
	
	public PackageCompletionProposal(String replacementString, int replacementOffset, int replacementLength, Image image, StyledString displayString, int relevance) {
		super(replacementString, replacementOffset, replacementLength, image,
				displayString, relevance);
	}
	
	/*
	 * (non-Javadoc)
	 * @see descent.internal.ui.text.java.AbstractJavaCompletionProposal#apply(org.eclipse.jface.text.IDocument, char, int)
	 */
	@Override
	public void apply(IDocument document, char trigger, int offset) {
		try {
			// patch replacement length
			int delta= offset - (getReplacementOffset() + getReplacementLength());
			if (delta > 0)
				setReplacementLength(getReplacementLength() + delta);
	
			boolean isSmartTrigger= isSmartTrigger(trigger);
	
			String replacement;
			if (isSmartTrigger || trigger == (char) 0) {
				replacement= getReplacementString();
			} else {
				StringBuffer buffer= new StringBuffer(getReplacementString());
				
				// If the trigger is a '.', we want to autocomplete just until the
				// next segment of the import
				if (trigger == '.') {
					int start = getStart(document, offset);
					int bufferOffset = offset - start;
					
					int indexOfNextDot = buffer.indexOf(".", bufferOffset);
					if (indexOfNextDot != -1) {
						buffer.setLength(indexOfNextDot + 1);
					} else {
						buffer.append('.');
					}
					setCursorPosition(buffer.length());
				} else if (trigger == ';') {
					buffer.append(';');
					setCursorPosition(buffer.length());
				} else {
					// fix for PR #5533. Assumes that no eating takes place.
					if ((getCursorPosition() > 0 && getCursorPosition() <= buffer.length() 
							&& buffer.charAt(getCursorPosition() - 1) != trigger
							&& trigger != '=' && trigger != ';')) {
						buffer.insert(getCursorPosition(), trigger);
						setCursorPosition(getCursorPosition() + 1);
					}
				}
	
				replacement= buffer.toString();
				setReplacementString(replacement);
			}
	
			// reference position just at the end of the document change.
			int referenceOffset= getReplacementOffset() + getReplacementLength();
			final ReferenceTracker referenceTracker= new ReferenceTracker();
			referenceTracker.preReplace(document, referenceOffset);
	
			replace(document, getReplacementOffset(), getReplacementLength(), replacement);
	
			referenceOffset= referenceTracker.postReplace(document);
			setReplacementOffset(referenceOffset - (replacement == null ? 0 : replacement.length()));
	
			// PR 47097
			if (isSmartTrigger)
				handleSmartTrigger(document, trigger, referenceOffset);
	
		} catch (BadLocationException x) {
			// ignore
		}
	}

	private int getStart(IDocument document, int offset) throws BadLocationException {
		offset--;
		
		while(true) {
			int whitespace = offset + 1;
			
			char c = document.getChar(offset);
			if(Character.isWhitespace(c)) {
				do { 
					c = document.getChar(--offset);
				} while(Character.isWhitespace(c));
			} else {
				offset--;
				continue;
			}
			
			if (c != 't') continue;
			
			c = document.getChar(--offset);
			if (c != 'r') continue;
			
			c = document.getChar(--offset);
			if (c != 'o') continue;
			
			c = document.getChar(--offset);
			if (c != 'p') continue;
			
			c = document.getChar(--offset);
			if (c != 'm') continue;
			
			c = document.getChar(--offset);
			if (c != 'i') continue;
			
			c = document.getChar(--offset);
			if (!Character.isJavaIdentifierPart(c)) {
				return whitespace;
			}
		}
	}

}
