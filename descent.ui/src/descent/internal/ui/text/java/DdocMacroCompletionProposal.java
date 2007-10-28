package descent.internal.ui.text.java;

import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import descent.core.CompletionProposal;
import descent.internal.ui.text.TextPresenter;
import descent.internal.ui.text.java.hover.AbstractReusableInformationControlCreator;
import descent.ui.text.java.JavaContentAssistInvocationContext;

public class DdocMacroCompletionProposal extends LazyJavaCompletionProposal {

	public DdocMacroCompletionProposal(CompletionProposal proposal, JavaContentAssistInvocationContext context) {
		super(proposal, context);
	}
	
	@Override
	protected String computeReplacementString() {
		return super.computeReplacementString() + "()"; //$NON-NLS-1$
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

}
