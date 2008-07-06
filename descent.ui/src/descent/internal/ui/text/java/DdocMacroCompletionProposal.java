package descent.internal.ui.text.java;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.LinkedModeUI;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.link.LinkedPositionGroup;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.link.EditorLinkedModeUI;

import descent.core.CompletionProposal;
import descent.internal.ui.JavaPlugin;
import descent.internal.ui.javaeditor.EditorHighlightingSynchronizer;
import descent.internal.ui.javaeditor.JavaEditor;
import descent.internal.ui.text.TextPresenter;
import descent.internal.ui.text.java.hover.AbstractReusableInformationControlCreator;
import descent.ui.text.java.JavaContentAssistInvocationContext;

public class DdocMacroCompletionProposal extends LazyJavaCompletionProposal {
	
	private int fMaxNumberOfParameters;
	private boolean fParametersHasPlus;
	private boolean fParametersHasZero;
	private boolean fParametersComputed;
	private int[] fParameterOffsets;
	
	private IRegion fSelectedRegion; // initialized by apply()

	public DdocMacroCompletionProposal(CompletionProposal proposal, JavaContentAssistInvocationContext context) {
		super(proposal, context);
	}
	
	@Override
	public void apply(IDocument document, char trigger, int offset) {
		super.apply(document, trigger, offset);
		
		int baseOffset= getReplacementOffset();
		String replacement= getReplacementString();
		
		if (hasParameters()) {
			try {
				LinkedModeModel model= new LinkedModeModel();
				for (int i= 0; i != fParameterOffsets.length; i++) {
					LinkedPositionGroup group= new LinkedPositionGroup();
					group.addPosition(new LinkedPosition(document, baseOffset + fParameterOffsets[i], 2, LinkedPositionGroup.NO_STOP));
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
		}
	}
	
	@Override
	protected String computeReplacementString() {
		String str = super.computeReplacementString();
		if (str.length() > 0 && str.charAt(0) == '$') {
			computeParameters();
			
			StringBuilder sb = new StringBuilder();
			sb.append('$');
			sb.append('(');
			sb.append(str.substring(1));
			
			if (hasParameters()) {
				// If we have parameters, don't care about $0 
				if (fMaxNumberOfParameters > 0) {
					// If it's $1 $+, we want two positions
					if (fParametersHasPlus && fMaxNumberOfParameters == 1) {
						fParameterOffsets = new int[2];
						sb.append(' ');
						fParameterOffsets[0] = sb.length();
						sb.append('$');
						sb.append('1');
						sb.append(',');
						sb.append(' ');
						fParameterOffsets[1] = sb.length();
						sb.append('$');
						sb.append('+');
					} else {
						fParameterOffsets = new int[fMaxNumberOfParameters];				
						sb.append(' ');
						for (int i = 1; i <= fMaxNumberOfParameters; i++) {
							if (i != 1) {
								sb.append(',');
								sb.append(' ');
							}
							fParameterOffsets[i - 1] = sb.length();
							sb.append('$');
							sb.append(i);
						}
					}
				} else {
					// Just $0
					fParameterOffsets = new int[1];
					sb.append(' ');
					fParameterOffsets[0] = sb.length();
					sb.append('$');
					sb.append('0');
				}
			}
			
			sb.append(')');
			return sb.toString();
		}
		return super.computeReplacementString() + "()"; //$NON-NLS-1$
	}
	
	private boolean hasParameters() {
		computeParameters();
		return fMaxNumberOfParameters != 0 || fParametersHasPlus || fParametersHasZero;
	}
	
	private void computeParameters() {
		if (fParametersComputed) {
			return;
		}
		fParametersComputed = true;
		
		fMaxNumberOfParameters = 0;
		char[] completion = fProposal.getName();
		for (int i = 0; i < completion.length; i++) {
			char c = completion[i];
			if (c == '$' && i < completion.length - 1) {
				i++;
				c = completion[i];
				int val = c - '0';
				if (val == 0) {
					fParametersHasZero = true;
				} else if (val > 0 && val <= 9) {
					if (val > fMaxNumberOfParameters) {
						fMaxNumberOfParameters = val;
					}
				} else if (c == '+') {
					fParametersHasPlus = true;
				}
			}
		}
	}

	private boolean containsPlaceholders(String completion) {
		return completion.matches(".*\\$(\\d).*");
	}

	@Override
	public void apply(ITextViewer viewer, char trigger, int stateMask, int offset) {
		super.apply(viewer, trigger, stateMask, offset);
		setCursorPosition(getCursorPosition() - 1);
	}
	
	@Override
	public IInformationControlCreator getInformationControlCreator() {
		return new AbstractReusableInformationControlCreator() {
			public IInformationControl doCreateInformationControl(Shell parent) {
				return new DefaultInformationControl(parent, SWT.NO_TRIM | SWT.TOOL, SWT.NONE, new TextPresenter());
			}
		}; 
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
	
	private void openErrorDialog(BadLocationException e) {
		Shell shell= getTextViewer().getTextWidget().getShell();
		MessageDialog.openError(shell, JavaTextMessages.ExperimentalProposal_error_msg, e.getMessage());
	}
	
	@Override
	public Point getSelection(IDocument document) {
		if (fSelectedRegion == null)
			return new Point(getReplacementOffset(), 0);

		return new Point(fSelectedRegion.getOffset(), fSelectedRegion.getLength());
	}

}
