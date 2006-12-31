package mmrnmhrm.ui.editors;

import mmrnmhrm.DeeUICore;
import mmrnmhrm.text.EDeePartitions;

import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.reconciler.MonoReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

public class DeeSourceViewerConfiguration extends SourceViewerConfiguration {

	public String getConfiguredDocumentPartitioning(ISourceViewer sourceViewer) {
		// TODO Auto-generated method stub
		return super.getConfiguredDocumentPartitioning(sourceViewer);
	}
	
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return EDeePartitions.legalContentTypes;
	}
	
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
	    PresentationReconciler reconciler = new PresentationReconciler();

	    DefaultDamagerRepairer dr = new DefaultDamagerRepairer(DeeUICore.getDefaultDeeCodeScanner());
	    reconciler.setDamager(dr, EDeePartitions.DEE_DEFAULT);
	    reconciler.setRepairer(dr, EDeePartitions.DEE_DEFAULT);

	    return reconciler;
	}
	
	public IReconciler getReconciler(ISourceViewer sourceViewer) {
		MonoReconciler reconciler = new MonoReconciler(
				new DeeReconcilingStrategy(), true);
		reconciler.install(sourceViewer);
		return reconciler;
	}

}
