package mmrnmhrm.ui.editor.text;

import java.util.Map;

import mmrnmhrm.ui.DeePlugin;
import mmrnmhrm.ui.editor.DeeReconcilingStrategy;
import mmrnmhrm.ui.text.DeeCodeScanner;
import mmrnmhrm.ui.text.IDeePartitions;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.reconciler.MonoReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.texteditor.ITextEditor;


/** Dee SourceViewer Configuration
 */
public class DeeSourceViewerConfiguration extends LangSourceViewerConfiguration {

	private ITextEditor fTextEditor;

	private DeeCodeScanner fCodeScanner;
	//private String fDocumentPartitioning;
	//private AbstractJavaScanner fMultilineCommentScanner;
	//private AbstractJavaScanner fSinglelineCommentScanner;
	//private AbstractJavaScanner fStringScanner;
	//private AbstractJavaScanner fJavaDocScanner;
	//private IColorManager fColorManager;
	//private JavaDoubleClickSelector fJavaDoubleClickSelector;
	
	public DeeSourceViewerConfiguration(ITextEditor textEditor, IPreferenceStore prefStore) {
		super(prefStore);
		this.fTextEditor = textEditor;
		fCodeScanner = DeePlugin.getDefaultDeeCodeScanner();
	}
	
	public boolean adaptToPreferenceChange(PropertyChangeEvent event) {
		return fCodeScanner.adaptToPreferenceChange(event);
	}
	
	@Override
	public String getConfiguredDocumentPartitioning(ISourceViewer sourceViewer) {
		return IDeePartitions.DEE_PARTITIONING;
	}

	@Override
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return IDeePartitions.legalContentTypes;
	}
	
	
	@Override
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
	    PresentationReconciler reconciler = new PresentationReconciler();
	    
	    reconciler.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));

	    DefaultDamagerRepairer dr = new DefaultDamagerRepairer(DeePlugin.getDefaultDeeCodeScanner());
	    reconciler.setDamager(dr, IDeePartitions.DEE_CODE);
	    reconciler.setRepairer(dr, IDeePartitions.DEE_CODE);

	    return reconciler;
	}
	
	@Override @SuppressWarnings("unchecked")
	protected Map getHyperlinkDetectorTargets(ISourceViewer sourceViewer) {
		Map targets= super.getHyperlinkDetectorTargets(sourceViewer);
		targets.put(DeeHyperlinkDetector.DEE_EDITOR_TARGET, fTextEditor); 
		return targets;
	}
	
	@Override
	public IReconciler getReconciler(ISourceViewer sourceViewer) {
		MonoReconciler reconciler = new MonoReconciler(
				new DeeReconcilingStrategy(), true);
		reconciler.install(sourceViewer);
		return reconciler;
	}
	
	@Override
	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
		ContentAssistant assistant = new ContentAssistant();

		IContentAssistProcessor deeContentAssistProcessor 
			= new DeeCodeContentAssistProcessor(assistant, fTextEditor);
		assistant.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));

		assistant.setRestoreCompletionProposalSize(getSettings("completion_proposal_size")); //$NON-NLS-1$

		assistant.setContentAssistProcessor(deeContentAssistProcessor, IDeePartitions.DEE_CODE);

		//assistant.setProposalPopupOrientation(IContentAssistant.CONTEXT_INFO_BELOW);
		assistant.enableAutoInsert(true);
		assistant.enablePrefixCompletion(true);
		Color colorWhite = Display.getDefault().getSystemColor(SWT.COLOR_WHITE);
		
		assistant.enableAutoActivation(true);
		assistant.setAutoActivationDelay(500);
		assistant.setProposalSelectorBackground(colorWhite);
		
		assistant.setContextInformationPopupOrientation(IContentAssistant.CONTEXT_INFO_ABOVE);
		assistant.setInformationControlCreator(getInformationControlCreator(sourceViewer));
		
		return assistant;
	}
	

	@Override
	public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType) {
		return new DeeTextHover(sourceViewer, fTextEditor);
	}

}
