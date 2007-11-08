package mmrnmhrm.ui.editor.text;


import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jface.text.AbstractReusableInformationControlCreator;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension2;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension3;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension5;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;

public abstract class AbstractCompletionProposal implements
		ICompletionProposal
		, ICompletionProposalExtension
		, ICompletionProposalExtension2
		, ICompletionProposalExtension3 
		, ICompletionProposalExtension5
		{
	

	/** The CSS used to format javadoc information. */
	private static String fgCSSStyles;

	
	/** The string to be displayed in the completion proposal popup. */
	protected String fDisplayString;
	/** The replacement string. */
	protected String fReplacementString;
	/** The replacement offset. */
	protected int fReplacementOffset;
	/** The replacement length. */
	protected int fReplacementLength;
	/** The cursor position after this proposal has been applied. */
	protected int fCursorPosition;
	/** The image to be displayed in the completion proposal popup. */
	protected Image fImage;
	/** The context information of this proposal. */
	protected IContextInformation fContextInformation;
	
	private IInformationControlCreator fCreator;

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
	public AbstractCompletionProposal(String replacementString,
			int replacementOffset, int replacementLength, int cursorPosition,
			Image image, String displayString,
			IContextInformation contextInformation) {
		Assert.isNotNull(replacementString);
		Assert.isTrue(replacementOffset >= 0);
		Assert.isTrue(replacementLength >= 0);
		Assert.isTrue(cursorPosition >= 0);

		fReplacementString= replacementString;
		fReplacementOffset= replacementOffset;
		fReplacementLength= replacementLength;
		fCursorPosition= cursorPosition;
		fImage= image;
		fDisplayString= displayString;
		fContextInformation= contextInformation;
	}
	
	/** Gets the replacement offset. */
	public int getReplacementOffset() {
		return fReplacementOffset;
	}

	/** Sets the replacement offset. */
	public void setReplacementOffset(int replacementOffset) {
		Assert.isTrue(replacementOffset >= 0);
		fReplacementOffset= replacementOffset;
	}
	
	/** Sets the replacement length.  */
	public void setReplacementLength(int replacementLength) {
		Assert.isTrue(replacementLength >= 0);
		fReplacementLength= replacementLength;
	}

	/** Gets the replacement length. */
	public int getReplacementLength() {
		return fReplacementLength;
	}

	/* --------------------------------- */


	/** {@inheritDoc} */
	public Point getSelection(IDocument document) {
		return new Point(fReplacementOffset + fCursorPosition, 0);
	}

	/** {@inheritDoc} */
	public IContextInformation getContextInformation() {
		return fContextInformation;
	}

	/** {@inheritDoc} */
	public Image getImage() {
		return fImage;
	}

	/** {@inheritDoc} */
	public String getDisplayString() {
		if (fDisplayString != null)
			return fDisplayString;
		return fReplacementString;
	}
	
	/** {@inheritDoc} */
	public String getAdditionalProposalInfo() {
		return null;
	}

	/** {@inheritDoc} */
	public void apply(IDocument document) {
		if(fReplacementLength == 0 && fReplacementString.length() == 0)
			return;
		try {
			document.replace(fReplacementOffset, fReplacementLength, fReplacementString);
		} catch (BadLocationException x) {
			// ignore
		}
	}

	
	public static char[] TRIGGER_CHARACTERS = new char[] { }; 

	public char[] getTriggerCharacters() {
		return TRIGGER_CHARACTERS;
	}
	
	public int getContextInformationPosition() {
		return fReplacementOffset + fCursorPosition;
	}

	
	public boolean isValidFor(IDocument document, int offset) {
		return validate(document, offset, null);
	}

	public void apply(IDocument document, char trigger, int offset) {
		// So far, I don't think validation is necessary
		apply(document);
	}


	/**
	 * Returns the text in <code>document</code> from {@link #getReplacementOffset()} to
	 * <code>offset</code>. Returns the empty string if <code>offset</code> is before the
	 * replacement offset or if an exception occurs when accessing the document.
	 */
	protected String getPrefix(IDocument document, int offset) {
		try {
			int length= offset - getReplacementOffset();
			if (length > 0)
				return document.get(getReplacementOffset(), length);
		} catch (BadLocationException x) {
		}
		return ""; //$NON-NLS-1$
	}



	public boolean isValidPrefix(String prefix) {
		return fReplacementString.startsWith(prefix);
	}
	
	public boolean validate(IDocument document, int offset, DocumentEvent event) {

		System.out.println("Validate" + offset +" "+ getReplacementOffset());
		if (offset < getReplacementOffset())
			return false;
		
		boolean validated= isValidPrefix(getPrefix(document, offset));

		if (validated && event != null) {
			// adapt replacement range to document change
			int delta= (event.fText == null ? 0 : event.fText.length()) - event.fLength;
			final int newLength= Math.max(getReplacementLength() + delta, 0);
			setReplacementLength(newLength);
		}

		return validated;
	}

	public void apply(ITextViewer viewer, char trigger, int stateMask, int offset) {
		IDocument document= viewer.getDocument();
		apply(document, trigger, offset);
	}

	public void selected(ITextViewer viewer, boolean smartToggle) {
		// Do nothing
	}

	public void unselected(ITextViewer viewer) {
		// Do nothing
	}

	public int getPrefixCompletionStart(IDocument document, int completionOffset) {
		return getReplacementOffset();
	}

	public CharSequence getPrefixCompletionText(IDocument document,
			int completionOffset) {
		// Check camel case here
		return fReplacementString;
	}
	

	/** Returns the style information for displaying HTML (Javadoc) content. */
	protected String getCSSStyles() {
		if (fgCSSStyles == null) {
			fgCSSStyles= HoverUtil.getDDocPreparedCSS("/JavadocHoverStyleSheet.css");
		}
		return fgCSSStyles;
	}
	
	@SuppressWarnings("restriction")
	public IInformationControlCreator getInformationControlCreator() {
		Shell shell= JavaPlugin.getActiveWorkbenchShell();
		if (shell == null
				|| !org.eclipse.jface.internal.text.html.BrowserInformationControl
						.isAvailable(shell))
			return null;
		
		if (fCreator == null) {
			fCreator= new ControlCreator();
		}
		return fCreator;
	}

	private static final class ControlCreator extends AbstractReusableInformationControlCreator {
		@SuppressWarnings("restriction")
		@Override
		public IInformationControl doCreateInformationControl(Shell parent) {
			return new org.eclipse.jface.internal.text.html.BrowserInformationControl(
					parent, SWT.NO_TRIM | SWT.TOOL, SWT.NONE, null);
		}
	}

	public Object getAdditionalProposalInfo(IProgressMonitor monitor) {
		//if (getProposalInfo() != null) {
			String info= getProposalInfoString(monitor);
			return HoverUtil.getCompleteHoverInfo(info, getCSSStyles());
		//}
	}
	
	protected abstract String getProposalInfoString(IProgressMonitor monitor);

	
}
