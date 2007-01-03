package mmrnmhrm.ui.editors;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.ui.texteditor.ITextEditor;

import util.AssertIn;

/**
 * Detects hyperlinks for D elements.
 */
public class DeeHyperlinkDetector implements IHyperlinkDetector {
	
	private ITextEditor fTextEditor;

	/** Creates a new D element hyperlink detector for given editor */
	public DeeHyperlinkDetector(ITextEditor editor) {
		AssertIn.isNotNull(editor);
		fTextEditor= editor;
	}

	public IHyperlink[] detectHyperlinks(ITextViewer textViewer,
			IRegion region, boolean canShowMultipleHyperlinks) {
		// TODO Detects hyperlinks for D elements.
		return null;
	}

}
