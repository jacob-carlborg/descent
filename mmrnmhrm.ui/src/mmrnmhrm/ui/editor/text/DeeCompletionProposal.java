package mmrnmhrm.ui.editor.text;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension3;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;

public class DeeCompletionProposal extends AbstractCompletionProposal implements
		ICompletionProposalExtension3, ICompletionProposalExtension {

	
	public DeeCompletionProposal(String replacementString,
			int replacementOffset, int replacementLength, int cursorPosition,
			Image image, String displayString,
			IContextInformation contextInformation) {
		super(replacementString, replacementOffset, replacementLength,
				cursorPosition, image, displayString, contextInformation, null);
	}

	public IInformationControlCreator getInformationControlCreator() {
		return null; // No custom control creator is available
	}

	public int getPrefixCompletionStart(IDocument document, int completionOffset) {
		return fReplacementOffset;
	}

	public CharSequence getPrefixCompletionText(IDocument document,
			int completionOffset) {
		return fReplacementString;
	}

	public void apply(IDocument document, char trigger, int offset) {
		apply(document);
	}

	public int getContextInformationPosition() {
		return fReplacementOffset;
	}

	public char[] getTriggerCharacters() {
		return new char[] { };
	}

	public boolean isValidFor(IDocument document, int offset) {
		return true;
	}
	
}
