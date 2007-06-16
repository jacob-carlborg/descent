package descent.internal.ui.text.correction;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.IContextInformation;

import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolution2;

import descent.internal.corext.util.Messages;

import descent.ui.text.java.IJavaCompletionProposal;

import descent.internal.ui.JavaPlugin;
import descent.internal.ui.JavaPluginImages;

/**
  */
public class MarkerResolutionProposal implements IJavaCompletionProposal {

	private IMarkerResolution fResolution;
	private IMarker fMarker;

	/**
	 * Constructor for MarkerResolutionProposal.
	 */
	public MarkerResolutionProposal(IMarkerResolution resolution, IMarker marker) {
		fResolution= resolution;
		fMarker= marker;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#apply(org.eclipse.jface.text.IDocument)
	 */
	public void apply(IDocument document) {
		fResolution.run(fMarker);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getAdditionalProposalInfo()
	 */
	public String getAdditionalProposalInfo() {
		if (fResolution instanceof IMarkerResolution2) {
			return ((IMarkerResolution2) fResolution).getDescription();
		}
		try {
			String problemDesc= (String) fMarker.getAttribute(IMarker.MESSAGE);
			return Messages.format(CorrectionMessages.MarkerResolutionProposal_additionaldesc, problemDesc);
		} catch (CoreException e) {
			JavaPlugin.log(e);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getContextInformation()
	 */
	public IContextInformation getContextInformation() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getDisplayString()
	 */
	public String getDisplayString() {
		return fResolution.getLabel();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getImage()
	 */
	public Image getImage() {
		if (fResolution instanceof IMarkerResolution2) {
			return ((IMarkerResolution2) fResolution).getImage();
		}
		return JavaPluginImages.get(JavaPluginImages.IMG_CORRECTION_CHANGE);
	}

	/* (non-Javadoc)
	 * @see descent.internal.ui.text.java.IJavaCompletionProposal#getRelevance()
	 */
	public int getRelevance() {
		if (fResolution instanceof IJavaMarkerResolutionExtension) {
			return ((IJavaMarkerResolutionExtension) fResolution).getRelevance();
		}
		return 10;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getSelection(org.eclipse.jface.text.IDocument)
	 */
	public Point getSelection(IDocument document) {
		return null;
	}

}
