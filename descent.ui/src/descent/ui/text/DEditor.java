package descent.ui.text;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditor;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import descent.core.dom.ICompilationUnit;
import descent.core.dom.IElement;
import descent.ui.text.outline.DEditorContentOutlinePage;

public class DEditor extends AbstractDecoratedTextEditor {

	private ColorManager colorManager;
	private DEditorContentOutlinePage outlinePage;
	private DReconcilingStrategy reconcilingStrategy;
	private IElement currentOutlineElement;

	public DEditor() {
		super();
		
		colorManager = new ColorManager();
		reconcilingStrategy = new DReconcilingStrategy(this);
		
		setSourceViewerConfiguration(new DConfiguration(this));
		setDocumentProvider(new DDocumentProvider());
	}
	
	/**
	 * Returns the document being edited.
	 */
	public IDocument getDocument() {
		return getSourceViewer().getDocument();
	}
	
	/**
	 * Returns the reconciling strategy of this editor.
	 */
	public IReconcilingStrategy getReconcilingStrategy() {
		return reconcilingStrategy;
	}
	
	/**
	 * Returns the compilation unit being edited.
	 */
	public ICompilationUnit getCompilationUnit() {
		return reconcilingStrategy.getCompilationUnit();
	}
	
	/**
	 * Returns the ColorManager of this editor.
	 */
	public ColorManager getColorManager() {
		return colorManager;
	}
	
	/**
	 * Updates the outline page.
	 */
	public void updateOutlinePage() {
		if (outlinePage != null) {
			outlinePage.update();
		}			
	}
	
	/**
	 * Returns the outline element under the cursor.	 */
	public IElement getOutlineElement() {
		return currentOutlineElement;
	}
	
	public void dispose() {
		colorManager.dispose();
		super.dispose();
	}
	
	@Override
	protected void doSetInput(IEditorInput input) throws CoreException {
		super.doSetInput(input);

		if (outlinePage != null)
			outlinePage.setInput(input);
	}
	
	protected void editorSaved() {
		super.editorSaved();
		
		if (outlinePage != null) {
			updateOutlinePage();
		}
	}
	
	@Override
	protected void handleCursorPositionChanged() {
		super.handleCursorPositionChanged();
		
		updateCurrentOutlineElement();
	}
	
	/**
	 * Updates the element under the cursor.
	 */
	public void updateCurrentOutlineElement() {
		int position = getCaretPosition();
		if (position == -1) return;
		
		currentOutlineElement = DDomUtil.getOutlineElementAt(
				getCompilationUnit(), position);
		if (currentOutlineElement == null) {
			resetHighlightRange();
			return;
		}
		
		if (outlinePage != null) {
			outlinePage.selectElement(currentOutlineElement);
		}
		
		highlightRangeForElement(currentOutlineElement, false);
	}
	
	/**
	 * Returns the caret position.
	 */
	public int getCaretPosition() {
		ISourceViewer sourceViewer = getSourceViewer();
		if (sourceViewer != null) {
			IDocument document = sourceViewer.getDocument();
			if (document != null) {
				StyledText styledText = sourceViewer.getTextWidget();
				int caret = widgetOffset2ModelOffset(sourceViewer,
													  styledText.getCaretOffset());
				
				return caret;
			}
		}
		return -1;
	}

	/**
	 * Highlights a range for an element in this editor. If the
	 * element has comments, the comments are also highlighted.
	 */
	public void highlightRangeForElement(IElement element, boolean moveCursor) {
		setHighlightRange(element.getStartPosition(), element.getLength(), moveCursor);
	}
	
	public Object getAdapter(Class required) {
		if (IContentOutlinePage.class.equals(required)) {
			if (outlinePage == null) {
				outlinePage = new DEditorContentOutlinePage(this);
				if (getEditorInput() != null)
					outlinePage.setInput(getEditorInput());
			}
			return outlinePage;
		}
		return super.getAdapter(required);
	}	

}
