package mmrnmhrm.ui.editor;

import melnorme.miscutil.log.Logg;
import mmrnmhrm.core.model.CompilationUnit;
import mmrnmhrm.ui.DeePlugin;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.ui.texteditor.ITextEditor;

public class DeeReconcilingStrategy implements IReconcilingStrategy {

	protected IDocument document;
	private ITextEditor textEditor;
	
	public DeeReconcilingStrategy(ITextEditor textEditor) {
		this.textEditor = textEditor;
	}

	public void setDocument(IDocument document) {
		this.document = document;
	}
	
	public void reconcile(IRegion partition) {
		Logg.model.println("Reconcile:", partition);
		CompilationUnit cunit = 
			DeePlugin.getCompilationUnitOperation(textEditor.getEditorInput());
		
		if(cunit != null)
			cunit.reconcile();
	}

	public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion) {
		reconcile(subRegion);
	}


}
