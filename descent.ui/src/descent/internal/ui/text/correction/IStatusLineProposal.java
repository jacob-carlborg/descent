package descent.internal.ui.text.correction;

/**
 * A proposal which is able to show a message
 * on the status line of the content assistant
 * in which this proposal is shown.
 * 
 * @see org.eclipse.jface.text.contentassist.IContentAssistantExtension2
 */
public interface IStatusLineProposal {

	/**
	 * The message to show when this proposal is
	 * selected by the user in the content assistant.
	 * 
	 * @return The message to show, or null for no message.
	 */
	public String getStatusMessage();

}
