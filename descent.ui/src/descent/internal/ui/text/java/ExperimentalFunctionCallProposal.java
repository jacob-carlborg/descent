package descent.internal.ui.text.java;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.LinkedModeUI;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.link.LinkedPositionGroup;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.link.EditorLinkedModeUI;

import descent.core.CompletionProposal;
import descent.core.Signature;
import descent.internal.ui.JavaPlugin;
import descent.internal.ui.javaeditor.EditorHighlightingSynchronizer;
import descent.internal.ui.javaeditor.JavaEditor;
import descent.ui.text.java.JavaContentAssistInvocationContext;

/**
 * An experimental proposal.
 */
public final class ExperimentalFunctionCallProposal extends JavaMethodCompletionProposal {

	private IRegion fSelectedRegion; // initialized by apply()
	private int[] fArgumentOffsets;
	private int[] fArgumentLengths;

	public ExperimentalFunctionCallProposal(CompletionProposal proposal, JavaContentAssistInvocationContext context) {
		super(proposal, context);
	}

	/*
	 * @see ICompletionProposalExtension#apply(IDocument, char)
	 */
	public void apply(IDocument document, char trigger, int offset) {
		super.apply(document, trigger, offset);
		int baseOffset= getReplacementOffset();
		String replacement= getReplacementString();

		if (!mustInsertDot(trigger) && fProposal.wantArguments() && fArgumentOffsets != null && getTextViewer() != null) {
			try {
				LinkedModeModel model= new LinkedModeModel();
				for (int i= 0; i != fArgumentOffsets.length; i++) {
					LinkedPositionGroup group= new LinkedPositionGroup();
					group.addPosition(new LinkedPosition(document, baseOffset + fArgumentOffsets[i], fArgumentLengths[i], LinkedPositionGroup.NO_STOP));
					model.addGroup(group);
				}

				model.forceInstall();
				JavaEditor editor= getJavaEditor();
				if (editor != null) {
					model.addLinkingListener(new EditorHighlightingSynchronizer(editor));
				}

				LinkedModeUI ui= new EditorLinkedModeUI(model, getTextViewer());
				ui.setExitPosition(getTextViewer(), baseOffset + replacement.length(), 0, Integer.MAX_VALUE);
				ui.setExitPolicy(new ExitPolicy(')', document));
				ui.setDoContextInfo(true);
				ui.setCyclingMode(LinkedModeUI.CYCLE_WHEN_NO_PARENT);
				ui.enter();

				fSelectedRegion= ui.getSelectedRegion();

			} catch (BadLocationException e) {
				JavaPlugin.log(e);
				openErrorDialog(e);
			}
		} else {
			fSelectedRegion= new Region(baseOffset + replacement.length(), 0);
		}
	}
	
	/*
	 * @see descent.internal.ui.text.java.JavaMethodCompletionProposal#needsLinkedMode()
	 */
	protected boolean needsLinkedMode() {
		return false; // we handle it ourselves
	}
	
	/*
	 * @see descent.internal.ui.text.java.LazyJavaCompletionProposal#computeReplacementString()
	 */
	protected String computeReplacementString() {
		try {
			if (!hasParameters() || !hasArgumentList()) {
				return super.computeReplacementString();
			}
			
			char[][] parameterTypes= Signature.getParameterTypes(fProposal.getTypeSignature());
			char[][] parameterNames = new char[parameterTypes.length][];
			for (int i = 0; i < parameterTypes.length; i++) {
				parameterNames[i] = ("p" + i).toCharArray();
			}
	
			int count= parameterNames.length;
			fArgumentOffsets= new int[count];
			fArgumentLengths= new int[count];
			StringBuffer buffer= new StringBuffer(String.valueOf(fProposal.getName()));
			
			FormatterPrefs prefs= getFormatterPrefs();
			
			if (prefs.beforeOpeningParen)
				buffer.append(SPACE);
			buffer.append(LPAREN);
			
			setCursorPosition(buffer.length());
			
			if (prefs.afterOpeningParen)
				buffer.append(SPACE);
			
			for (int i= 0; i != count; i++) {
				if (i != 0) {
					if (prefs.beforeFunctionComma)
						buffer.append(SPACE);
					buffer.append(COMMA);
					if (prefs.afterFunctionComma)
						buffer.append(SPACE);
				}
				
				fArgumentOffsets[i]= buffer.length();
				buffer.append(parameterNames[i]);
				fArgumentLengths[i]= parameterNames[i].length;
			}
			
			if (prefs.beforeFunctionClosingParen)
				buffer.append(SPACE);
	
			buffer.append(RPAREN);
	
			return buffer.toString();
		} finally {
			if (!fProposal.wantArguments()) {
				setCursorPosition(fProposal.getName().length);
				return new String(fProposal.getName());
			}
		}
	}

	/**
	 * Returns the currently active java editor, or <code>null</code> if it
	 * cannot be determined.
	 *
	 * @return  the currently active java editor, or <code>null</code>
	 */
	private JavaEditor getJavaEditor() {
		IEditorPart part= JavaPlugin.getActivePage().getActiveEditor();
		if (part instanceof JavaEditor)
			return (JavaEditor) part;
		else
			return null;
	}

	/*
	 * @see ICompletionProposal#getSelection(IDocument)
	 */
	public Point getSelection(IDocument document) {
		if (fSelectedRegion == null)
			return new Point(getReplacementOffset(), 0);

		return new Point(fSelectedRegion.getOffset(), fSelectedRegion.getLength());
	}
	
	@Override
	protected boolean computeIsSetter() throws IllegalArgumentException {
		return false;
	}
	
	@Override
	protected boolean computeIsGetter() throws IllegalArgumentException {
		return false;
	}

	private void openErrorDialog(BadLocationException e) {
		Shell shell= getTextViewer().getTextWidget().getShell();
		MessageDialog.openError(shell, JavaTextMessages.ExperimentalProposal_error_msg, e.getMessage());
	}

}
