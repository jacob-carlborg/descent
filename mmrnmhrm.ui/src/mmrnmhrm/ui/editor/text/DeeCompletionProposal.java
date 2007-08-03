package mmrnmhrm.ui.editor.text;

import melnorme.miscutil.ExceptionAdapter;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension5;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;

import dtool.dom.definitions.DefUnit;

public class DeeCompletionProposal extends AbstractCompletionProposal implements
		ICompletionProposalExtension
		, ICompletionProposalExtension5
		{

	
	private DefUnit defUnit;

	/**
	 * Creates a new completion proposal. All fields are initialized based on the provided information.
	 *
	 * @param replacementString the actual string to be inserted into the document
	 * @param replacementOffset the offset of the text to be replaced
	 * @param replacementLength the length of the text to be replaced
	 * @param cursorPosition the position of the cursor following the insert relative to replacementOffset
	 * @param image the image to display for this proposal
	 * @param displayString the string to be displayed for the proposal
	 * @param contextInformation the context information associated with this proposal
	 * @param additionalProposalInfo the additional information associated with this proposal
	 */
	public DeeCompletionProposal(String replacementString,
			int replacementOffset, int replacementLength, int cursorPosition,
			Image image, String displayString,
			DefUnit defUnit,
			IContextInformation contextInformation) {
		super(replacementString, replacementOffset, replacementLength,
				cursorPosition, image, displayString, contextInformation, null);
		this.defUnit = defUnit;
	}


	public int getPrefixCompletionStart(IDocument document, int completionOffset) {
		return fReplacementOffset;
	}

	public CharSequence getPrefixCompletionText(IDocument document,
			int completionOffset) {
		return fReplacementString;
	}
	
	public char[] getTriggerCharacters() {
		return new char[] { };
	}
	
	public int getContextInformationPosition() {
		return fReplacementOffset + fCursorPosition;
	}
	
	public void apply(IDocument document, char trigger, int offset) {
		fReplacementLength += offset - fReplacementOffset;
		apply(document);
	}


	/** Filter proposals for keys that the user has placed meanwhile. */
	public boolean isValidFor(IDocument document, int offset) {
		System.out.println("Called isValidFor, offset:" + offset);
		if(offset < fReplacementOffset)
			return false;
		try {
			String deltaStr = document.get(fReplacementOffset, offset - fReplacementOffset);
			if (fReplacementString.length() >= deltaStr.length()
					&& fReplacementString.startsWith(deltaStr))
				return true;
		} catch (BadLocationException e) {
			throw ExceptionAdapter.unchecked(e);
		}
		return false;
	}

	public String getProposalInfoString(IProgressMonitor monitor) {
		return HoverUtil.getDefUnitHoverInfoWithDeeDoc(defUnit);
	}

}
