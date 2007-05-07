package mmrnmhrm.ui.editor;

import mmrnmhrm.ui.DeePlugin;
import mmrnmhrm.ui.editor.text.DeeHyperlinkDetector;
import mmrnmhrm.ui.text.EDeePartitions;

import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.reconciler.MonoReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * DeeSourceViewConfiguration
 * XXX: Java extends TextSourceViewerConfiguration ??
 */
public class DeeSourceViewerConfiguration extends SourceViewerConfiguration {

	
	private ITextEditor fTextEditor;
	//private String fDocumentPartitioning;
	//private AbstractJavaScanner fCodeScanner;
	//private AbstractJavaScanner fMultilineCommentScanner;
	//private AbstractJavaScanner fSinglelineCommentScanner;
	//private AbstractJavaScanner fStringScanner;
	//private AbstractJavaScanner fJavaDocScanner;
	//private IColorManager fColorManager;
	//private JavaDoubleClickSelector fJavaDoubleClickSelector;


	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return EDeePartitions.legalContentTypes;
	}
	
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
	    PresentationReconciler reconciler = new PresentationReconciler();

	    DefaultDamagerRepairer dr = new DefaultDamagerRepairer(DeePlugin.getDefaultDeeCodeScanner());
	    reconciler.setDamager(dr, EDeePartitions.DEE_CODE);
	    reconciler.setRepairer(dr, EDeePartitions.DEE_CODE);

	    return reconciler;
	}
	
	
	
	@Override
	public IHyperlinkDetector[] getHyperlinkDetectors(ISourceViewer sourceViewer) {
/*		if (!fPreferenceStore.getBoolean(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_HYPERLINKS_ENABLED))
			return null;
*/
		IHyperlinkDetector[] inheritedDetectors= super.getHyperlinkDetectors(sourceViewer);

		if (fTextEditor == null)
			return inheritedDetectors;

		int inheritedDetectorsLength= inheritedDetectors != null ? inheritedDetectors.length : 0;
		IHyperlinkDetector[] detectors= new IHyperlinkDetector[inheritedDetectorsLength + 1];
		detectors[0]= new DeeHyperlinkDetector(fTextEditor);
		for (int i= 0; i < inheritedDetectorsLength; i++)
			detectors[i+1]= inheritedDetectors[i];

		return detectors;
	}
	
	public IReconciler getReconciler(ISourceViewer sourceViewer) {
		MonoReconciler reconciler = new MonoReconciler(
				new DeeReconcilingStrategy(), true);
		reconciler.install(sourceViewer);
		return reconciler;
	}
	
	

}
