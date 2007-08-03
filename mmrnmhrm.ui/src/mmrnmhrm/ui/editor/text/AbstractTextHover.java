package mmrnmhrm.ui.editor.text;

import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.DefaultTextHover;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.ITextHoverExtension;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.texteditor.ITextEditor;

public class AbstractTextHover extends DefaultTextHover {

	/** The CSS used to format javadoc information. */
	protected static String fgCSSStyles;

	protected ITextEditor fEditor;

	
	public AbstractTextHover(ISourceViewer sourceViewer) {
		super(sourceViewer);
	}

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

	public IInformationControlCreator getHoverControlCreator() {
		return new IInformationControlCreator() {
			@SuppressWarnings("restriction")
			public IInformationControl createInformationControl(Shell parent) {
				return new DefaultInformationControl(parent, SWT.NONE, 
						new	org.eclipse.jface.internal.text.html.HTMLTextPresenter(true),
						EditorsUI.getTooltipAffordanceString());
			}
		};
	}

}