package descent.internal.ui.text.correction;

import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;

import org.eclipse.core.runtime.CoreException;

import org.eclipse.jface.text.IDocument;

import descent.core.ICompilationUnit;

import descent.internal.ui.JavaPluginImages;

public class ReplaceCorrectionProposal extends CUCorrectionProposal {

	private String fReplacementString;
	private int fOffset;
	private int fLength;

	public ReplaceCorrectionProposal(String name, ICompilationUnit cu, int offset, int length, String replacementString, int relevance) {
		super(name, cu, relevance, JavaPluginImages.get(JavaPluginImages.IMG_CORRECTION_CHANGE));
		fReplacementString= replacementString;
		fOffset= offset;
		fLength= length;
	}

	/* (non-Javadoc)
	 * @see descent.internal.ui.text.correction.CUCorrectionProposal#addEdits(org.eclipse.jface.text.IDocument)
	 */
	protected void addEdits(IDocument doc, TextEdit rootEdit) throws CoreException {
		super.addEdits(doc, rootEdit);

		TextEdit edit= new ReplaceEdit(fOffset, fLength, fReplacementString);
		rootEdit.addChild(edit);
	}

}
