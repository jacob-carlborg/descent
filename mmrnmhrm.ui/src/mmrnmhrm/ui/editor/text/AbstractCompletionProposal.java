package mmrnmhrm.ui.editor.text;


import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.AbstractReusableInformationControlCreator;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension3;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;

public abstract class AbstractCompletionProposal implements
		ICompletionProposal, ICompletionProposalExtension3 {

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
	/** The additional info of this proposal. */
	protected String fAdditionalProposalInfo;
	
	private IInformationControlCreator fCreator;


	/**
	 * Creates a new completion proposal based on the provided information. The replacement string is
	 * considered being the display string too. All remaining fields are set to <code>null</code>.
	 *
	 * @param replacementString the actual string to be inserted into the document
	 * @param replacementOffset the offset of the text to be replaced
	 * @param replacementLength the length of the text to be replaced
	 * @param cursorPosition the position of the cursor following the insert relative to replacementOffset
	 */
	public AbstractCompletionProposal(String replacementString, int replacementOffset, int replacementLength, int cursorPosition) {
		this(replacementString, replacementOffset, replacementLength, cursorPosition, null, null, null, null);
	}

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
			IContextInformation contextInformation,
			String additionalProposalInfo) {
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
		fAdditionalProposalInfo= additionalProposalInfo;
	}
	
	
	private static final class ControlCreator extends AbstractReusableInformationControlCreator {

		@SuppressWarnings("restriction")
		public IInformationControl doCreateInformationControl(Shell parent) {
			return new org.eclipse.jface.internal.text.html.BrowserInformationControl(
					parent, SWT.NO_TRIM | SWT.TOOL, SWT.NONE, null);
		}
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
		return fAdditionalProposalInfo;
	}
	
	public Object getAdditionalProposalInfo(IProgressMonitor monitor) {
		//if (getProposalInfo() != null) {
			String info= getProposalInfoString(monitor);
			return HoverUtil.getCompleteHoverInfo(info, getCSSStyles());
		//}
	}

	/**
	 * Returns the style information for displaying HTML (Javadoc) content.
	 * 
	 * @return the CSS styles 
	 * @since 3.3
	 */
	@SuppressWarnings("restriction")
	protected String getCSSStyles() {
		if (fgCSSStyles == null) {
			fgCSSStyles= HoverUtil.loadStyleSheet("/JavadocHoverStyleSheet.css");
		}
		String css= fgCSSStyles;
		if (css != null) {
			FontData fontData= JFaceResources.getFontRegistry().getFontData(PreferenceConstants.APPEARANCE_JAVADOC_FONT)[0];
			css= org.eclipse.jface.internal.text.html.
				HTMLPrinter.convertTopLevelFont(css, fontData);
		}
		return css;
	}

	protected abstract String getProposalInfoString(IProgressMonitor monitor);
	
}
